package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-04-20 15:30
 **/
@Data
public class CustomerFieldResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String returnCode;
    private String errorMsg;
    private Detail body;

    @Data
    public static class Detail implements Serializable {
        private static final long serialVersionUID = 2L;

        private String fieldKey;
        private String errorMessage;
    }
}
