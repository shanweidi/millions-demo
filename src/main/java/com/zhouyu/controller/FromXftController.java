package com.zhouyu.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zhouyu.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author shanweidi
 * @since 2026-05-11 16:53
 **/
@RestController
@RequestMapping("/nc")
public class FromXftController {

    private static final Logger logger = LoggerFactory.getLogger(FromXftController.class);
    @PostMapping({"/push/person"})
    public Result<String> pushPerson(@RequestBody String data) {
        logger.info("pushPerson decrypt data:"+data);
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
        logger.info("changePerson decrypt data:"+data);
        HttpResponse response = null;
        try {
            response = HttpRequest.post("http://10.1.9.155:2333/service/XChangeServlet?account=001&groupcode=G07")
                    .addHeaders(MapUtil.<String, String>builder()
                            .put("Content-Type", "application/xml").build())
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
}
