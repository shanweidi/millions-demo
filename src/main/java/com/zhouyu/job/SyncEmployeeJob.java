package com.zhouyu.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.domain.TStfSeqDO;
import com.zhouyu.service.TMapFieldService;
import com.zhouyu.service.TStfSeqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;

/**
 *
 * @author shanweidi
 * @since 2026-04-17 08:34
 **/
//@Component
public class SyncEmployeeJob extends HengdianJob {

    private static final Logger logger = LoggerFactory.getLogger(SyncEmployeeJob.class);

    private List<String> objectFields = Arrays.asList("staffBasicInfo","staffHrmInfo","staffWagesAndSocialSecurityInfo","staffEmergencyContact","StaffGroupServiceInfo","staffTaxBasicInfo");
    @Resource
    private TStfSeqService service;

    @Resource
    private TMapFieldService fieldService;

    @Resource
    private Gson gson;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;


    @Value("${xft.edit-url}")
    private String editUrl;

    private LoadingCache<String, List<Map<String, Object>>> lruCache;

    @PostConstruct
    public void buildCache() {
        lruCache = CacheBuilder.newBuilder()
                .maximumSize(100L)
                .build(new CacheLoader<String, List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> load(String key) throws Exception {
                        String[] split = key.split(":");
                        String sql = "SELECT * FROM " + split[0] + " WHERE PK_PSNDOC = ?";
                        return jdbcTemplate.queryForList(sql, split[1]);
                    }
                });
    }

    @Override
    protected void scheduleCronTask() {
        logger.info("=========人员信息同步开始============");
        List<TStfSeqDO> needSync = service.selectNeedSync();
        if (needSync.isEmpty()) {
            return;
        }
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery()
                .eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getNeedSync,1));
        JsonArray jsonArray = new JsonArray();
        for (TStfSeqDO stfSeqDO : needSync) {
            //todo 组装json
            JsonObject employee = new JsonObject();
            for (TMapFieldDO field : fields) {
                List<Map<String, Object>> ncEntity = lruCache.getUnchecked(buildCacheKey(field.getInnerTableName(), stfSeqDO.getPkPsndoc()));
                if (objectFields.contains(field.getOutTab())) {//ncEntity是单元素集合
                    if (employee.has(field.getOutTab())) {
                        JsonObject existObj = employee.getAsJsonObject(field.getOutTab());
                        if (field.getOutField() != null) {//非自定义字段
                            existObj.addProperty(field.getOutField(),ncEntity.get(0).get(field.getInnerField()).toString());
                        } else {
                            JsonObject customerElement = new JsonObject();
                            customerElement.addProperty("classKey",field.getOutFieldClasskey());
                            customerElement.addProperty("fieldKey",field.getOutFieldFieldkey());
                            customerElement.addProperty("fieldValue",ncEntity.get(0).get(field.getInnerField()).toString());
                            if (existObj.has("customerFieldInfoList")) {
                                existObj.getAsJsonArray("customerFieldInfoList").add(customerElement);
                            } else {
                                JsonArray customerFieldInfoList = new JsonArray();
                                customerFieldInfoList.add(customerElement);
                                existObj.add("customerFieldInfoList",customerFieldInfoList);
                            }
                        }
                    } else {
                        JsonObject fieldObj = new JsonObject();
                        if (field.getOutField() != null) {//非自定义字段
                            fieldObj.addProperty(field.getOutField(),ncEntity.get(0).get(field.getInnerField()).toString());
                        } else {
                            JsonObject customerElement = new JsonObject();
                            customerElement.addProperty("classKey",field.getOutFieldClasskey());
                            customerElement.addProperty("fieldKey",field.getOutFieldFieldkey());
                            customerElement.addProperty("fieldValue",ncEntity.get(0).get(field.getInnerField()).toString());

                            JsonArray customerFieldInfoList = new JsonArray();
                            customerFieldInfoList.add(customerElement);
                            fieldObj.add("customerFieldInfoList",customerFieldInfoList);
                        }
                        employee.add(field.getOutTab(),fieldObj);
                    }
                } else {//ncEntity可能是多元素集合

                }
            }
            JsonObject element = employee.getAsJsonObject("staffBasicInfo");
            element.addProperty("stfSeq",stfSeqDO.getStfSeq());
            jsonArray.add(employee);
        }

        JsonObject result = new JsonObject();
        result.add("staffInfoList",jsonArray);
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);
        String jsonBody = gson.toJson(result);

        //加密和解密密文的key
        String key = secret.substring(0, 32);
        String digest;
        try {
            String secretMsg = Sm4Util.encryptEcb(key, jsonBody);
            Map<String, String> newRequestBodyMap = new HashMap<>();
            //secretMsg这个名称固定
            newRequestBodyMap.put("secretMsg", secretMsg);
            digest = gson.toJson(newRequestBodyMap);
        } catch (Exception e) {
            logger.error("generate x-alb-digest error:",e);
            return;
        }

        try {

        } catch (Exception e) {
            logger.error("edit staffInfo http call error:",e);
        }

        logger.info("=========人员信息同步结束============");
    }

    @Override
    protected String springDynamicCron() {
        return "0 */5 * * * ?";
    }

    private String buildCacheKey(String table,String id) {
        Preconditions.checkNotNull(table,"用友表名不能为空");
        return table.trim() + ":" + id;
    }


//      private List<EmployeeDTO> convert(List<BdPsndocDO> needAdd) {
//        return needAdd.stream().filter(e -> StringUtils.hasLength(e.getName())).map(entity -> {
//            EmployeeDTO.HrInfo hrInfo = new EmployeeDTO.HrInfo();
//            hrInfo.setEntryDate(LocalDate.parse(entity.getGlbdef5(), formatter));
//
//            EmployeeDTO.BasicInfo basicInfo = EmployeeDTO.BasicInfo.builder().stfNumber(entity.getCode()).stfName(entity.getName())
//                    .mobileNumber(entity.getMobile()).certificateType("A").certificateNumber(entity.getId())
//                    .birthday(LocalDate.parse(entity.getBirthdate(), formatter))
//                    .sex(Objects.equals(entity.getSex(), 1) ? "0" : "1")
//                    .build();
//            return new EmployeeDTO(basicInfo,hrInfo);
//        }).collect(Collectors.toList());
//    }
}
