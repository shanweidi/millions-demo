package com.zhouyu;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
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
import com.google.gson.JsonObject;
import com.zhouyu.domain.BdPsndocDO;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.CustomerFieldDTO;
import com.zhouyu.dto.CustomerFieldResponse;
import com.zhouyu.dto.EmployeeEditDTO;
import com.zhouyu.service.BdPsndocService;
import com.zhouyu.service.DeptService;
import com.zhouyu.service.TMapFieldService;
import com.zhouyu.service.TMapFileldValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 *
 * @author shanweidi
 * @date 2026-03-13 12:48
 **/
@SpringBootTest
public class AppTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TMapFileldValueService tMapFileldValueService;

    @Autowired
    TMapFieldService fieldService;

    @Autowired
    DeptService deptService;

    static final String SQL_TEMPLATE = "select * from bd_psndoc where ENABLESTATE = ? AND CREATIONTIME > ?";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    LoadingCache<String, List<Map<String, Object>>> cache;

    @PostConstruct
    public void buildCache() {
        cache = CacheBuilder.newBuilder()
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


    @Autowired
    Gson gson;

    @Autowired
    BdPsndocService service;

    @Test
    public void testGson() {
        List<BdPsndocDO> result = service.list(Wrappers.<BdPsndocDO>lambdaQuery()
                        .eq(BdPsndocDO::getEnablestate,2).eq(BdPsndocDO::getName,"单伟迪"));
        for (BdPsndocDO bd : result) {
            EmployeeEditDTO.StaffInfo staffInfo = new EmployeeEditDTO.StaffInfo();
            JsonObject jsonObject = gson.toJsonTree(staffInfo).getAsJsonObject();
            System.out.println(jsonObject.toString());
            JsonObject basicInfo = new JsonObject();
            basicInfo.addProperty("stfName",bd.getName());
            basicInfo.addProperty("birthday",bd.getBirthdate());
            jsonObject.add("staffBasicInfo",basicInfo);
            jsonObject.add("staffHrmInfo",new JsonObject());
            System.out.println(jsonObject.toString());
        }

    }

    @Test
    public void testEncrypt() {
        String secret = "5460f088209068b3";
        byte[] bytes = secret.getBytes();

        AES aes = SecureUtil.aes(bytes);
        String sss = aes.encryptBase64("<?xml version=\"1.0\" encoding='UTF-8'?>\n" +
                "<ufinterface account=\"001\" billtype=\"stapply\" filename=\"\" groupcode=\"G07\" isexchange=\"Y\" replace=\"Y\" roottag=\"\" sender=\"xft\">\n" +
                "    <bill id=\"\">\n" +
                "        <billhead>\n" +
                "            <pk_group>G07</pk_group>\n" +
                "            <pk_org>0001A11000000036R13R</pk_org>\n" +
                "            <fun_code>60090transapply</fun_code>\n" +
                "            <pk_billtype>6113</pk_billtype>\n" +
                "            <billmaker>hgwxl</billmaker>\n" +
                "            <apply_date>2026-06-29</apply_date>\n" +
                "            <pk_psndoc>20042177</pk_psndoc>\n" +
                "            <stapply_mode>1</stapply_mode>\n" +
                "            <pk_trnstype>0303</pk_trnstype>\n" +
                "            <sreason>0304</sreason>\n" +
                "            <effectdate>2026-06-30</effectdate>\n" +
                "            <trial_flag>11</trial_flag>\n" +
                "            <memo>22</memo>\n" +
                "            <oldpk_org>0001A11000000036R13R</oldpk_org>\n" +
                "            <newpk_org>0001A110000000009CWH</newpk_org>\n" +
                "            <newpk_psncl>01</newpk_psncl>\n" +
                "            <newpk_dept>1008ZZ1000000000AFO1</newpk_dept>\n" +
                "            <newpk_post>PB00000051</newpk_post>\n" +
                "            <newpk_postseries>01</newpk_postseries>\n" +
                "            <newpk_jobgrade>0105</newpk_jobgrade>\n" +
                "            <newpk_jobrank>M4</newpk_jobrank>\n" +
                "            <newpk_job></newpk_job>\n" +
                "            <newseries>10702</newseries>\n" +
                "            <newjobglbdef3>11</newjobglbdef3>\n" +
                "            <newjobglbdef4>11</newjobglbdef4>\n" +
                "            <newjobglbdef7>07</newjobglbdef7>\n" +
                "            <newjobglbdef14></newjobglbdef14>\n" +
                "            <pk_hi_org>0001A110000000009CWH</pk_hi_org>\n" +
                "            <pk_hrcm_org>0001A110000000009CWH</pk_hrcm_org>\n" +
                "            <isrelease>N</isrelease>\n" +
                "            <isend>N</isend>\n" +
                "            <ifendpart>N</ifendpart>\n" +
                "            <ifsynwork>68944</ifsynwork>\n" +
                "            <newpoststat>N</newpoststat>\n" +
                "            <ishrssbill>63823</ishrssbill>\n" +
                "            <ifaddblack></ifaddblack>\n" +
                "            <ifaddpsnchg></ifaddpsnchg>\n" +
                "            <isneedfile>N</isneedfile>\n" +
                "            <creator>hgwxl</creator>\n" +
                "            <creationtime></creationtime>\n" +
                "            <approver>hgwxl</approver>\n" +
                "            <approve_state>调动审批状态test</approve_state>\n" +
                "            <approve_note>调动审批批语test</approve_note>\n" +
                "        </billhead>\n" +
                "    </bill>\n" +
                "</ufinterface>");
        System.out.println(sss);
    }

    @Test
    public void testList() {
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery()
                .eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getInnerTableName, "hi_psndoc_work")
                .eq(TMapFieldDO::getNeedSync, 1));
        JsonObject employee = new JsonObject();
        employee.add("customerFieldInfoList",new JsonArray());
        for (TMapFieldDO field : fields) {
            String key = "hi_psndoc_work:0001ZZ1000000002D914";
            List<Map<String, Object>> ncEntity = cache.getUnchecked(key);
            if (employee.has(field.getOutTab())) {
                JsonArray existArray = employee.getAsJsonArray(field.getOutTab());
                if (field.getOutField() != null) {//非自定义字段
                    for (int i = 0; i < ncEntity.size(); i++) {
                        JsonObject objInList = (JsonObject) existArray.get(i);
                        objInList.addProperty(field.getOutField(),ncEntity.get(i).get(field.getInnerField()).toString());
                    }
                } else {
                    for (int i = 0; i < ncEntity.size(); i++) {
                        JsonObject customerElement = new JsonObject();
                        customerElement.addProperty("classKey",field.getOutFieldClasskey());
                        customerElement.addProperty("fieldKey",field.getOutFieldFieldkey());
                        customerElement.addProperty("fieldValue",ncEntity.get(i).get(field.getInnerField()).toString());
                    }
                }
            }
        }

    }

    @Test
    public void testObj() {
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery()
                .eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getOutTab, "staffBasicInfo")
                .eq(TMapFieldDO::getNeedSync, 1));
        JsonArray jsonArray = new JsonArray();

        JsonObject employee = new JsonObject();
        employee.add("customerFieldInfoList",new JsonArray());
        //先处理业务单元
        String deptId = jdbcTemplate.queryForObject("select PK_DEPT from HI_PSNJOB where PK_PSNDOC = ? and ISMAINJOB = ? and POSTSTAT = ?", String.class, "0001ZZ1000000002D914", "Y", "Y");
        String deptInnerCode = deptService.getCodeByDeptId(deptId);
        String deptOutCode = tMapFileldValueService.queryOutValue(2L, deptInnerCode);
        JsonObject staffBasicInfo = new JsonObject();
        staffBasicInfo.addProperty("orgSeq",deptOutCode);
        employee.add("staffBasicInfo",staffBasicInfo);

        for (TMapFieldDO field : fields) {
            if ("orgSeq".equals(field.getOutField())) { //该字段已特殊处理
                continue;
            }
            List<Map<String, Object>> ncEntity = cache.getUnchecked(buildCacheKey(field.getInnerTableName(), "0001ZZ1000000002D914"));
            if (ncEntity.get(0).get(field.getInnerField().toUpperCase()) == null) {
                // 若NC表中该字段值为null,直接跳过组装
                continue;
            }

            if (employee.has(field.getOutTab())) {
                JsonObject existObj = employee.getAsJsonObject(field.getOutTab());
                if (field.getOutField() != null) {//非自定义字段
                    if (field.getNeedTranslate() == 0) {
                        existObj.addProperty(field.getOutField(),ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString());
                    } else {
                        String tmp = translate(ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString(),field.getOutField(),field.getId());
                        existObj.addProperty(field.getOutField(),tmp);
                    }
                } else {
                    JsonObject customerElement = new JsonObject();
                    customerElement.addProperty("classKey",field.getOutFieldClasskey());
                    customerElement.addProperty("fieldKey",field.getOutFieldFieldkey());
                    customerElement.addProperty("fieldValue",ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString());

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
                    if (field.getNeedTranslate() == 0) {
                        fieldObj.addProperty(field.getOutField(),ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString());
                    } else {
                        String tmp = translate(ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString(),field.getOutField(),field.getId());
                        fieldObj.addProperty(field.getOutField(),tmp);
                    }
                } else {
                    JsonObject customerElement = new JsonObject();
                    customerElement.addProperty("classKey",field.getOutFieldClasskey());
                    customerElement.addProperty("fieldKey",field.getOutFieldFieldkey());
                    customerElement.addProperty("fieldValue",ncEntity.get(0).get(field.getInnerField().toUpperCase()).toString());

                    JsonArray customerFieldInfoList = new JsonArray();
                    customerFieldInfoList.add(customerElement);
                    fieldObj.add("customerFieldInfoList",customerFieldInfoList);
                }
                employee.add(field.getOutTab(),fieldObj);
            }
        }
        JsonObject element = employee.getAsJsonObject("staffBasicInfo");
        element.addProperty("stfSeq","testId");
        jsonArray.add(employee);
        System.out.println("result:"+jsonArray);
    }

    private String translate(String innerValue,String outField,Long fieldId){
        switch (outField) {
            case "certificateType" :
                String innerCode = jdbcTemplate.queryForObject("select CODE from BD_PSNIDTYPE where PK_IDENTITYPE = ?", String.class, innerValue);
                return tMapFileldValueService.queryOutValue(fieldId,innerCode);
            case "sex" :
                return innerValue.equals("1") ? "0" : "1";
            default:
                throw new RuntimeException("该字段暂不支持翻译:"+fieldId);
        }
    }

    private String buildCacheKey(String table,String id) {
        Preconditions.checkNotNull(table,"用友表名不能为空");
        return table.trim() + ":" + id;
    }

    @Value("${xft.field-url}")
    private String fieldUrl;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @Test
    public void testSyncHR019_0xx() {
        String sql = "select t1.code,t1.name  \n" +
                "from bd_defdoc t1 left join bd_defdoclist t2 on t1.pk_defdoclist=t2.pk_defdoclist \n" +
                "where t1.dr=0 and t1.enablestate=2 and t2.dr=0 and t2.code='HR019_0xx'  \n" +
                "and exists (select *from hi_psndoc_title  t where t.pk_techposttitle = t1.pk_defdoc)\n" +
                "order by t1.code";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(137L);
        List<TMapFileldValueDO> fieldValues = maps.stream()
                .map(e -> {
                    TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
                    tMapFileldValueDO.setMapFieldId(137L);
                    tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
                    tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
                    tMapFileldValueDO.setOutValue((String) e.get("CODE"));
                    tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
                    tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                    return tMapFileldValueDO;
                }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"S09TTITLE","FLD1100112");
    }

    @Test
    public void testSync46() {
        String sql = "select t1.code,t1.name  \n" +
                "from bd_defdoc t1 left join bd_defdoclist t2 on t1.pk_defdoclist=t2.pk_defdoclist \n" +
                "where t1.dr=0 and t1.enablestate=2 and t2.dr=0 and t2.code='HR010_0xx'  \n" +
                "and exists (select *from hi_psndoc_edu t where t.majortype = t1.pk_defdoc)\n" +
                "order by t1.code";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        List<String> list = maps.stream().map(e -> {
            return (String) e.get("CODE");
        }).collect(Collectors.toList());
        System.out.println(list);
        List<TMapFileldValueDO> result = tMapFileldValueService.queryByFieldId(46L).stream().filter(e -> list.contains(e.getInnerValue())).collect(Collectors.toList());
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        CustomerFieldDTO dto = new CustomerFieldDTO();
        dto.setClassKey("S07EDUCA");
        dto.setFieldKey("FLD1100075");
        dto.setFieldType("23");
        List<CustomerFieldDTO.OptionValue> values = result.stream().map(e -> {
            CustomerFieldDTO.OptionValue optionValue = new CustomerFieldDTO.OptionValue();
            optionValue.setOptionCode(e.getInnerValue());
            optionValue.setOptionValue(e.getInnerValueDesc());
            optionValue.setOptionSeq(e.getId().toString());
            return optionValue;
        }).collect(Collectors.toList());
        dto.setOptionValueDtoList(values);

        String jsonBody = gson.toJson(dto);

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
            return;
        }

        try {
            HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, fieldUrl, queryParam, digest);
            String responseData = postResult.getBody();
            responseData = Sm4Util.decryptEcb(key, responseData);
            CustomerFieldResponse response = gson.fromJson(responseData, CustomerFieldResponse.class);
            if ("SUC0000".equals(response.getReturnCode()) && "".equals(response.getBody().getErrorMessage())) {
                //同步成功
                System.out.println("SUCCESS!!!!");
            } else {
                System.out.println("save customer field business fail:"+responseData);
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    private void sendSyncHttp(List<TMapFileldValueDO> fieldValues,String classKey,String fieldKey) {
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        CustomerFieldDTO dto = new CustomerFieldDTO();
        dto.setClassKey(classKey);
        dto.setFieldKey(fieldKey);
        dto.setFieldType("23");
        List<CustomerFieldDTO.OptionValue> values = fieldValues.stream().map(e -> {
            CustomerFieldDTO.OptionValue optionValue = new CustomerFieldDTO.OptionValue();
            optionValue.setOptionCode(e.getInnerValue());
            optionValue.setOptionValue(e.getInnerValueDesc());
            optionValue.setOptionSeq(e.getId().toString());
            return optionValue;
        }).collect(Collectors.toList());
        dto.setOptionValueDtoList(values);

        String jsonBody = gson.toJson(dto);

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
            return;
        }

        try {
            HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, fieldUrl, queryParam, digest);
            String responseData = postResult.getBody();
            responseData = Sm4Util.decryptEcb(key, responseData);
            CustomerFieldResponse response = gson.fromJson(responseData, CustomerFieldResponse.class);
            if ("SUC0000".equals(response.getReturnCode()) && "".equals(response.getBody().getErrorMessage())) {
                //同步成功
                System.out.println("SUCCESS!!!!");
            } else {
                System.out.println("save customer field business fail:"+responseData);
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @Test
    public void testSyncJobLevel() {
        String sql = "select code,name  From om_joblevel a\n" +
                "where exists (select *from hi_psnjob b  where a.pk_joblevel = b.pk_jobgrade)\n" +
                " order by  code";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(306L);
        List<TMapFileldValueDO> fieldValues = maps.stream().filter(e -> !e.get("NAME").toString().contains("锐智九州"))
                .filter(e -> !e.get("CODE").toString().equals("0905"))
                .map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(306L);
            tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
            tMapFileldValueDO.setOutValue((String) e.get("CODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100054","FLD1100405");
    }
    @Test
    public void testSyncJobType() {
        String sql = "select jobtypecode,jobtypename  From om_jobtype a\n" +
                "where exists (select *from hi_psnjob b  where a.pk_jobtype = b.SERIES)\n" +
                " order by jobtypecode";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(304L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(304L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBTYPECODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBTYPENAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBTYPECODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100054","FLD1100404");
    }

    @Test
    public void testSyncCategory() {
        String sql = "select code,name from bd_psncl";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(301L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(301L);
            tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
            tMapFileldValueDO.setOutValue((String) e.get("CODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100054","FLD1100328");
    }

    @Test
    public void testSyncJobRank() {
        String sql = "select jobrankcode,jobrankname from om_jobrank";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(307L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(307L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBRANKCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBRANKNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBRANKCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100054","FLD1100406");
    }

    //同步岗位
    @Test
    public void testSyncPost() {
        String sql = "select postseriescode,postseriesname  from om_postseries";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(303L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(303L);
            tMapFileldValueDO.setInnerValue((String) e.get("POSTSERIESCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("POSTSERIESNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("POSTSERIESCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100054","FLD1100403");
    }

    //同步职务
    @Test
    public void testSyncJob() {
        String sql = "select distinct jobcode ,jobname  from om_job b \n" +
                "\twhere enablestate =2  \n" +
                "\tand exists (select *from hi_psnjob a where \t a.poststat ='Y' and  a.endflag ='N' and a.pk_job = b.pk_job)";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(255L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(255L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);

        sendSyncHttp(fieldValues,"CLS1100053","FLD1100367");

    }

    //调配类型
    @Test
    public void testSyncTrnsType() {
        String sql = "select trnstypecode as code,trnstypename as name  from hr_trnstype";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(276L);
        List<TMapFileldValueDO> fieldValues = maps.stream()
                .filter(e -> !e.get("CODE").toString().equals("0501")).map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(276L);
            tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
            tMapFileldValueDO.setOutValue((String) e.get("CODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100057","FLD1100373");
    }

    @Test
    public void testSyncBD004_0xx() {
        String sql = "select t1.code,t1.name  \n" +
                "from bd_defdoc t1 left join bd_defdoclist t2 on t1.pk_defdoclist=t2.pk_defdoclist \n" +
                "where t1.dr=0 and t1.enablestate=2 and t2.dr=0 and t2.code='BD004_0xx'  \n" +
                "and exists (select *from hi_psndoc_nationduty t where t.occuptype = t1.pk_defdoc)";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(230L);
        List<TMapFileldValueDO> fieldValues = maps.stream()
                .map(e -> {
                    TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
                    tMapFileldValueDO.setMapFieldId(230L);
                    tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
                    tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
                    tMapFileldValueDO.setOutValue((String) e.get("CODE"));
                    tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
                    tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                    return tMapFileldValueDO;
                }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100287");
    }

}
