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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
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
        //SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue());
        //byte[] bytes = secretKey.getEncoded();
        String secret = "5460f088209068b3";
        byte[] bytes = secret.getBytes();

        AES aes = SecureUtil.aes(bytes);
        String sss = aes.encryptBase64("{\"batchId\":\"1100001702996\",\"items\":[{\"pk_psndoc_code\":\"20042168\",\"approve_state\":\"2\"}]}");
        System.out.println(sss);
        String result = aes.decryptStr(sss);
        System.out.println(result);
    }

    @Test
    public void testCache() {
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery()
                .eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getInnerTableName, "hi_psndoc_work")
                .eq(TMapFieldDO::getNeedSync, 1));
        for (TMapFieldDO field : fields) {
            String key = "hi_psndoc_work:0001H5100000000BCDLE";
            List<Map<String, Object>> value = cache.getUnchecked(key);
            System.out.println(value);
        }

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
    public void testSync137() {
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
        int affect = tMapFileldValueService.removeByFieldId(224L);
        List<TMapFileldValueDO> fieldValues = maps.stream().filter(e -> !e.get("NAME").toString().contains("锐智九州"))
                .filter(e -> !e.get("CODE").toString().equals("0905"))
                .map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(224L);
            tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
            tMapFileldValueDO.setOutValue((String) e.get("CODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100281");
    }
    @Test
    public void testSyncJobType() {
        String sql = "select jobtypecode,jobtypename  From om_jobtype a\n" +
                "where exists (select *from hi_psnjob b  where a.pk_jobtype = b.SERIES)\n" +
                " order by jobtypecode";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(219L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(219L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBTYPECODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBTYPENAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBTYPECODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100276");
    }

    @Test
    public void testSyncCategory() {
        String sql = "select code,name from bd_psncl";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(225L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(225L);
            tMapFileldValueDO.setInnerValue((String) e.get("CODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("NAME"));
            tMapFileldValueDO.setOutValue((String) e.get("CODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100282");
    }

    @Test
    public void testSyncJobRank() {
        String sql = "select jobrankcode,jobrankname from om_jobrank";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(223L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(223L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBRANKCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBRANKNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBRANKCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100280");
    }

    //同步岗位
    @Test
    public void testSyncPost() {
        String sql = "select postseriescode,postseriesname  from om_postseries";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(221L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(221L);
            tMapFileldValueDO.setInnerValue((String) e.get("POSTSERIESCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("POSTSERIESNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("POSTSERIESCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);
        sendSyncHttp(fieldValues,"CLS1100056","FLD1100278");
    }

    //同步职务
    @Test
    public void testSyncJob() {
        String sql = "select distinct jobcode ,jobname  from om_job b \n" +
                "\twhere enablestate =2  \n" +
                "\tand exists (select *from hi_psnjob a where \t a.poststat ='Y' and  a.endflag ='N' and a.pk_job = b.pk_job)";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps);
        int affect = tMapFileldValueService.removeByFieldId(220L);
        List<TMapFileldValueDO> fieldValues = maps.stream().map(e -> {
            TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
            tMapFileldValueDO.setMapFieldId(220L);
            tMapFileldValueDO.setInnerValue((String) e.get("JOBCODE"));
            tMapFileldValueDO.setInnerValueDesc((String) e.get("JOBNAME"));
            tMapFileldValueDO.setOutValue((String) e.get("JOBCODE"));
            tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
            tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
            return tMapFileldValueDO;
        }).collect(Collectors.toList());
        tMapFileldValueService.saveBatch(fieldValues);

        sendSyncHttp(fieldValues,"CLS1100056","FLD1100277");

    }

}
