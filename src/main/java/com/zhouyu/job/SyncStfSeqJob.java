package com.zhouyu.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.gson.Gson;
import com.zhouyu.domain.BdPsndocDO;
import com.zhouyu.domain.TStfSeqDO;
import com.zhouyu.dto.EmployeeQuery;
import com.zhouyu.dto.QueryResponse;
import com.zhouyu.service.BdPsndocService;
import com.zhouyu.service.TStfSeqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author shanweidi
 * @since 2026-05-07 15:23
 **/
//@Component
public class SyncStfSeqJob extends HengdianJob {
    private static final Logger logger = LoggerFactory.getLogger(SyncStfSeqJob.class);

    @Resource
    private BdPsndocService service;

    @Resource
    private TStfSeqService stfSeqService;

    @Resource
    private Gson gson;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @Value("${xft.query-url}")
    private String queryUrl;
    @Override
    protected void scheduleCronTask() {
        logger.info("=========员工主键查询开始============");
        List<BdPsndocDO> result = service.list(Wrappers.<BdPsndocDO>lambdaQuery()
                .eq(BdPsndocDO::getEnablestate,2)
                .gt(BdPsndocDO::getCreationtime, LocalDateTime.now().format(formatter)));
        if (result.isEmpty()) {
            return;
        }
        List<TStfSeqDO> resultList = new ArrayList<>();
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);
        for (BdPsndocDO psndocDO : result) {
            TStfSeqDO exist =  stfSeqService.getOneByPsndoc(psndocDO.getPkPsndoc());
            if (exist != null) {
                continue;
            }
            EmployeeQuery body = EmployeeQuery.buildDefault(psndocDO.getId());
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
                HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, queryUrl, queryParam, digest);
                String responseData = postResult.getBody();
                responseData = Sm4Util.decryptEcb(key, responseData);
                QueryResponse response = gson.fromJson(responseData, QueryResponse.class);
                logger.info(responseData);
                if (SUCCESS.equals(response.getReturnCode()) && response.getBody().getRecords().size() > 0) {

                    TStfSeqDO needSave = new TStfSeqDO();
                    needSave.setStfSeq(response.getBody().getRecords().get(0).getStaffSeq());
                    needSave.setPkPsndoc(psndocDO.getPkPsndoc());
                    needSave.setCertificateNumber(psndocDO.getId());
                    needSave.setCreateTime(LocalDateTime.now().format(formatter));
                    needSave.setIsSync(0);
                    resultList.add(needSave);
                }
            } catch (Exception e) {
                logger.error("query staffInfo http call error:",e);
            }
        }
        stfSeqService.saveBatch(resultList);
        logger.info("=========员工主键查询结束============");
    }

    @Override
    protected String springDynamicCron() {
        return "0 0 4 * * ?";
    }
}
