package com.zhouyu.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmb.xft.open.api.BaseReqInf;
import com.cmb.xft.open.api.HttpResponseData;
import com.cmb.xft.open.api.Sm4Util;
import com.cmb.xft.open.api.XftOpenApiReqClient;
import com.google.gson.Gson;
import com.zhouyu.domain.TDictMapDO;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.CustomerFieldDTO;
import com.zhouyu.dto.CustomerFieldResponse;
import com.zhouyu.service.TDictMapService;
import com.zhouyu.service.TMapFieldService;
import com.zhouyu.service.TMapFileldValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author shanweidi
 * @since 2026-04-17 09:49
 **/
@Component
public class SyncCustomerFieldJob extends HengdianJob {

    private static final Logger logger = LoggerFactory.getLogger(SyncCustomerFieldJob.class);

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

    @Resource
    private TDictMapService dictMapService;

    @Override
    protected void scheduleCronTask() {
        logger.info("=========自定义字段同步开始============");
        List<TMapFieldDO> fields = fieldService.list(Wrappers.<TMapFieldDO>lambdaQuery().eq(TMapFieldDO::getIsDelete, 0)
                .eq(TMapFieldDO::getNeedSync,1).in(TMapFieldDO::getFieldType, "2","3")
                .eq(TMapFieldDO::getIsSync, 0));
        if (fields.isEmpty()) {
            return;
        }
        for (TMapFieldDO field : fields) {
            if (StringUtils.isEmpty(field.getOutFieldClasskey()) || StringUtils.isEmpty(field.getOutFieldFieldkey())) {
                continue;
            }
            if ("2".equals(field.getFieldType())) {
                fieldValueService.removeByFieldId(field.getId());
                List<TDictMapDO> dictMapDOS = dictMapService.queryByCode(field.getInnerFieldKey());
                List<TMapFileldValueDO> fieldValues = dictMapDOS.stream().map(e -> {
                    TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
                    tMapFileldValueDO.setMapFieldId(field.getId());
                    tMapFileldValueDO.setInnerValue(e.getKey());
                    tMapFileldValueDO.setInnerValueDesc(e.getValue());
                    tMapFileldValueDO.setOutValue(e.getKey());
                    tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
                    tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                    return tMapFileldValueDO;
                }).collect(Collectors.toList());
                fieldValueService.saveBatch(fieldValues);
            } else if ("3".equals(field.getFieldType())) {
                fieldValueService.removeByFieldId(field.getId());
                List<Map<String, String>> mappingList = fieldValueService.selectMapsByCode(field.getInnerFieldKey());
                List<TMapFileldValueDO> fieldValues = mappingList.stream().map(e -> {
                    TMapFileldValueDO tMapFileldValueDO = new TMapFileldValueDO();
                    tMapFileldValueDO.setMapFieldId(field.getId());
                    tMapFileldValueDO.setInnerValue(e.get("CODE"));
                    tMapFileldValueDO.setInnerValueDesc(e.get("NAME"));
                    tMapFileldValueDO.setOutValue(e.get("CODE"));
                    tMapFileldValueDO.setCreateTime(LocalDateTime.now().format(formatter));
                    tMapFileldValueDO.setUpdateTime(LocalDateTime.now().format(formatter));
                    return tMapFileldValueDO;
                }).collect(Collectors.toList());
                fieldValueService.saveBatch(fieldValues);
            }
        }
        long timestamp = Instant.now().toEpochMilli();
        BaseReqInf baseReqInf = new BaseReqInf(appId, secret);
        Map<String, Object> queryParam = new TreeMap<>();
        queryParam.put("CSCAPPUID", appId);
        queryParam.put("CSCPRJCOD",companyId);
        queryParam.put("CSCREQTIM",timestamp);

        for (TMapFieldDO field : fields) {
            if (StringUtils.isEmpty(field.getOutFieldClasskey()) || StringUtils.isEmpty(field.getOutFieldFieldkey())) {
                continue;
            }
            CustomerFieldDTO dto = new CustomerFieldDTO();
            dto.setClassKey(field.getOutFieldClasskey());
            dto.setFieldKey(field.getOutFieldFieldkey());
            dto.setFieldType(field.getOutFieldType().toString());
            if (23 == field.getOutFieldType()) {
                List<TMapFileldValueDO> valueList = fieldValueService.queryByFieldId(field.getId());
                List<CustomerFieldDTO.OptionValue> values = valueList.stream().map(e -> {
                    CustomerFieldDTO.OptionValue optionValue = new CustomerFieldDTO.OptionValue();
                    optionValue.setOptionCode(e.getInnerValue());
                    optionValue.setOptionValue(e.getInnerValueDesc());
                    optionValue.setOptionSeq(e.getId().toString());
                    return optionValue;
                }).collect(Collectors.toList());
                dto.setOptionValueDtoList(values);
            }

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
                    fieldService.update(Wrappers.<TMapFieldDO>lambdaUpdate().eq(TMapFieldDO::getId,field.getId())
                            .set(TMapFieldDO::getIsSync,1));
                } else {
                    logger.warn("save customer field business fail:"+responseData);
                }
            } catch (Exception e) {
                logger.error("save customer field http call error:",e);
            }
        }
        logger.info("=========自定义字段同步结束============");
    }

    @Override
    protected String springDynamicCron() {
        return "0 0 5 * * ?";
        //return "0 */3 * * * ?";
    }
}
