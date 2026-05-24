package com.zhouyu.job;

import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author shanweidi
 * @since 2026-04-28 10:05
 **/
//@Component
public class QueryTestJob extends HengdianJob {

    private static final Logger logger = LoggerFactory.getLogger(QueryTestJob.class);

    @Resource
    private Gson gson;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @Value("${xft.query-test-url}")
    private String testUrl;
    @Override
    protected void scheduleCronTask() {
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        Map<String, Boolean> bodyMap = new HashMap<>();
        bodyMap.put("openStatus", true);
        String jsonBody = gson.toJson(bodyMap);

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
            HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, testUrl, queryParam, digest);
            String responseData = postResult.getBody();
            responseData = Sm4Util.decryptEcb(key, responseData);
            logger.info(responseData);
        } catch (Exception e) {
            logger.error("query test error:",e);
        }
    }

    @Override
    protected String springDynamicCron() {
        return "0 */2 * * * ?";
    }
}
