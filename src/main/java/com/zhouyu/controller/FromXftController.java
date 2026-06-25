package com.zhouyu.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.Result;
import com.zhouyu.exception.GlobalErrorConstants;
import com.zhouyu.service.DeptService;
import com.zhouyu.service.TMapFileldValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 *
 * @author shanweidi
 * @since 2026-05-11 16:53
 **/
@RestController
@RequestMapping("/nc")
public class FromXftController {

    private static final Logger logger = LoggerFactory.getLogger(FromXftController.class);
    private List<String> parseTags = Arrays.asList("newpk_org","newpk_dept","newpk_post");

    @Resource
    private TMapFileldValueService valueService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private DeptService deptService;

    @PostMapping({"/push/person"})
    public Result<String> pushPerson(@RequestBody String data) {
        logger.info("pushPerson decrypt data:{}", data);
        HttpResponse response = null;
        try {
            response = HttpRequest.post("http://10.1.9.155:2333/service/IPushPersonToBdPsndocApi")
                    .addHeaders(MapUtil.<String, String>builder()
                            .put("Content-Type", "application/json").build())
                    .timeout(5000)
                    .body(data)
                    .execute();
            return Result.success(response.body());
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    @PostMapping({"/change/person"})
    public Result<String> changePerson(@RequestBody String data) {
        logger.info("changePerson decrypt data:{}", data);
        Document document = XmlUtil.parseXml(data);
        Node node = XmlUtil.getNodeByXPath(
                "//ufinterface/bill/billhead",
                document);
        Map<String, Object> snapshot = XmlUtil.xmlToMap(node);
        if (snapshot.containsKey("newpk_post")) {
            TMapFileldValueDO valueDO = valueService.getOne(Wrappers.<TMapFileldValueDO>lambdaQuery().eq(TMapFileldValueDO::getMapFieldId, 193)
                    .eq(TMapFileldValueDO::getOutValue, snapshot.get("newpk_post")));
            if (valueDO == null) {
                return Result.error(GlobalErrorConstants.BAD_POST);
            }
            //查询岗位code
            List<Map<String, Object>> postMapping = jdbcTemplate.queryForList("SELECT POSTCODE FROM OM_POST WHERE PK_DEPT = ? AND POSTNAME = ?", snapshot.get("newpk_dept"), valueDO.getInnerValue());
            if (postMapping.isEmpty()) {
                return Result.error(GlobalErrorConstants.BAD_RELATION);
            }
            Object postcode = postMapping.get(0).get("POSTCODE");
            snapshot.put("newpk_post",postcode);
        }
        if (snapshot.containsKey("pk_org")) {
            String orgCode = deptService.getCodeByOrgId(snapshot.get("pk_org").toString());
            snapshot.put("pk_org",orgCode);
        }
        String deptCode = deptService.getCodeByDeptId(snapshot.get("newpk_dept").toString());
        String[] split = deptCode.split("_");
        snapshot.put("newpk_org",split[0]);
        snapshot.put("newpk_dept",split[1]);

        //回写xml
        Element billHead = (Element) node;
        NodeList children = billHead.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String nodeName = childNode.getNodeName();
            if (snapshot.containsKey(nodeName)) {
                childNode.setTextContent(snapshot.get(nodeName).toString());
            }
        }
        String newXml = XmlUtil.toStr(document);
        logger.info("new xml:{}",newXml);

        HttpResponse response = null;
        try {
            response = HttpRequest.post("http://10.1.9.155:2333/service/XChangeServlet?account=001&groupcode=G07")
                    .addHeaders(MapUtil.<String, String>builder()
                            .put("Content-Type", "application/xml").build())
                    .timeout(5000)
                    .body(newXml)
                    .execute();
            return Result.success(response.body());
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }
}
