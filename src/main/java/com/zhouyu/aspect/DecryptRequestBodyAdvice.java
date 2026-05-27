package com.zhouyu.aspect;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author shanweidi
 * @since 2026-05-12 15:12
 **/
@ControllerAdvice(assignableTypes = {com.zhouyu.controller.FromXftController.class})
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private static final AES aes = SecureUtil.aes("5460f088209068b3".getBytes());
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String requestBody = StrUtil.utf8Str(IoUtil.readBytes(inputMessage.getBody()));
        try {
            String decryptedData = aes.decryptStr(requestBody);
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream(decryptedData.getBytes(StandardCharsets.UTF_8));
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        } catch (Exception e) {
            throw new IllegalArgumentException("decrypt fail:"+e.getMessage());
        }
    }
}
