package com.zhouyu.job;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.gson.Gson;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.CustomerFieldDTO;
import com.zhouyu.dto.CustomerFieldResponse;
import com.zhouyu.service.TMapFieldService;
import com.zhouyu.service.TMapFileldValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 检查已同步NC自定义档案是否有新增选项
 * @author shanweidi
 * @since 2026-06-23 13:54
 **/
@Component
public class CheckDocumentJob extends HengdianJob {

    private static final Logger logger = LoggerFactory.getLogger(CheckDocumentJob.class);

    @Value("${xft.field-url}")
    private String fieldUrl;

    @Value("${xft.app-id}")
    private String appId;

    @Value("${xft.company-id}")
    private String companyId;

    @Value("${xft.authority-secret}")
    private String secret;

    @Resource
    private Gson gson;

    @Resource
    private TMapFieldService fieldService;

    @Resource
    private TMapFileldValueService fieldValueService;
    @Override
    protected void scheduleCronTask() {
        logger.info("=========检查已同步自定义档案开始============");
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery().eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getOutFieldType,23).eq(TMapFieldDO::getFieldType, "3")
                .eq(TMapFieldDO::getIsSync, 1));
        if (fields.isEmpty()) {
            logger.info("=========检查已同步自定义档案结束============");
            return;
        }

        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        for (TMapFieldDO field : fields) {
            if (StrUtil.isEmpty(field.getOutFieldClasskey()) || StrUtil.isEmpty(field.getOutFieldFieldkey())) {
                continue;
            }
            List<TMapFileldValueDO> syncedValues = fieldValueService.queryByFieldId(field.getId());
            List<Map<String, String>> ncDocuments = fieldValueService.selectMapsByCode(field.getInnerFieldKey());
            if (ncDocuments.size() >= 1000 || syncedValues.size() == ncDocuments.size()) {
                continue;
            }

            List<TMapFileldValueDO> newFieldValues = ncDocuments.stream().map(e -> {
                TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
                tMapFileldValueDO.setMapFieldId(field.getId());
                tMapFileldValueDO.setInnerValue(e.get("CODE"));
                tMapFileldValueDO.setInnerValueDesc(e.get("NAME"));
                tMapFileldValueDO.setOutValue(e.get("CODE"));
                tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
                tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                return tMapFileldValueDO;
            }).collect(Collectors.toList());

            CustomerFieldDTO dto = new CustomerFieldDTO();
            dto.setClassKey(field.getOutFieldClasskey());
            dto.setFieldKey(field.getOutFieldFieldkey());
            dto.setFieldType(field.getOutFieldType().toString());
            List<CustomerFieldDTO.OptionValue> values = newFieldValues.stream().map(e -> {
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
                logger.error("generate x-alb-digest error:",e);
                return;
            }

            try {
                HttpResponseData postResult = XftOpenApiReqClient.doCommonPostReq(baseReqInf, fieldUrl, queryParam, digest);
                String responseData = postResult.getBody();
                responseData = Sm4Util.decryptEcb(key, responseData);
                CustomerFieldResponse response = gson.fromJson(responseData, CustomerFieldResponse.class);
                if (SUCCESS.equals(response.getReturnCode()) && "".equals(response.getBody().getErrorMessage())) {
                    //同步成功
                    logger.info("update customer field {} success!",field.getId());
                    fieldValueService.removeByFieldId(field.getId());
                    fieldValueService.saveBatch(newFieldValues);
                } else {
                    logger.warn("update customer field {} business fail:{}",field.getId(),responseData);
                }
            } catch (Exception e) {
                logger.error("update customer field http call error:",e);
            }
        }

        logger.info("=========检查已同步自定义档案结束============");
    }

    @Override
    protected String springDynamicCron() {
        return "0 0 6 * * ?";
    }
}
