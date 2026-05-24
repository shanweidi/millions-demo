package com.zhouyu.controller;

import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.gson.Gson;
import com.zhouyu.dto.SaveResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author shanweidi
 * @since 2026-03-20 15:19
 **/
@RestController
@RequestMapping("/xft")
public class FromNcController {

    private static final Logger logger = LoggerFactory.getLogger(FromNcController.class);

    @Resource
    private Gson gson;

    @Value("${xft.save-url}")
    private String saveUrl;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @PostMapping({"/save/candidate"})
    public SaveResponse saveCandidate(@RequestBody String data) {
        //加密和解密密文的key
        String key = secret.substring(0, 32);
        String digest;
        try {
            String secretMsg = Sm4Util.encryptEcb(key, data);
            Map<String, String> newRequestBodyMap = new HashMap<>();
            //secretMsg这个名称固定
            newRequestBodyMap.put("secretMsg", secretMsg);
            digest = gson.toJson(newRequestBodyMap);
        } catch (Exception e) {
            logger.error("generate x-alb-digest error:",e);
            return SaveResponse.failed(e.getMessage());
        }
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        try {
            HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, saveUrl, queryParam, digest);
            String responseData = postResult.getBody();
            responseData = Sm4Util.decryptEcb(key, responseData);
            SaveResponse response = gson.fromJson(responseData, SaveResponse.class);
            return response;
        } catch (Exception e) {
            logger.error("save staff http call error:",e);
            return SaveResponse.failed(e.getMessage());
        }
    }
}
