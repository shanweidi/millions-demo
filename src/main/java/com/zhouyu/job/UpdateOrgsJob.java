package com.zhouyu.job;

import cn.hutool.core.collection.CollUtil;
import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.OrgQuery;
import com.zhouyu.dto.OrgResponse;
import com.zhouyu.service.TMapFileldValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author shanweidi
 * @since 2026-07-01 16:41
 **/
@Component
public class UpdateOrgsJob extends HengdianJob {
    private static final Logger logger = LoggerFactory.getLogger(UpdateOrgsJob.class);

    @Resource
    private Gson gson;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @Value("${xft.org-url}")
    private String orgUrl;

    @Resource
    private TMapFileldValueService fieldValueService;

    @Override
    protected void scheduleCronTask() {
        logger.info("=========更新组织任务开始============");
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        List<OrgResponse.OrgInfo> allOrg = Lists.newArrayListWithCapacity(6000);
        List<OrgResponse.OrgInfo> currentPage = null;
        int index = 1;
        do {
            OrgQuery body = OrgQuery.buildOrgQuery(index);
            String jsonBody = gson.toJson(body);
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
                HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, orgUrl, queryParam, digest);
                String responseData = postResult.getBody();
                responseData = Sm4Util.decryptEcb(key, responseData);
                OrgResponse response = gson.fromJson(responseData, OrgResponse.class);

                if (!SUCCESS.equals(response.getReturnCode())) {
                    logger.error(responseData);
                    break;
                }
                currentPage = response.getBody().getRecords();
                if (response.getBody().getRecords().size() > 0) {
                    allOrg.addAll(currentPage);
                }
                index++;
            } catch (Exception e) {
                logger.error("query orgList http call error:",e);
            }
        } while (CollUtil.isNotEmpty(currentPage));

        Set<String> dbExist = fieldValueService.queryByFieldId(2L).stream().map(TMapFileldValueDO::getInnerValue).collect(Collectors.toSet());
        Set<String> fromXft = allOrg.stream().map(OrgResponse.OrgInfo::getCode).collect(Collectors.toSet());
        //薪福通中有，映射表里没有
        List<String> needRemove = Sets.difference(fromXft, dbExist).stream().collect(Collectors.toList());
        if (!needRemove.isEmpty()) {
            fieldValueService.removeByFieldId(2L);
            List<TMapFileldValueDO> result = allOrg.stream().map(e -> {
                TMapFileldValueDO valueDO = new TMapFileldValueDO();
                valueDO.setMapFieldId(2L);
                valueDO.setInnerValue(e.getCode());
                valueDO.setInnerValueDesc(e.getName());
                valueDO.setOutValue(e.getId());
                valueDO.setCreateTime(LocalDateTime.now().format(formatter));
                valueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                return valueDO;
            }).collect(Collectors.toList());
            fieldValueService.saveBatch(result);
        }
        logger.info("=========更新组织任务结束============");
    }

    @Override
    protected String springDynamicCron() {
        return "0 0 1 * * ?";
    }
}
