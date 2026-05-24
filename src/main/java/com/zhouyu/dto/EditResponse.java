package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-26 15:23
 **/
@Data
public class EditResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String returnCode;
    private String errorMsg;
    private List<EditDetail> body;

    @Data
    static class EditDetail implements Serializable{
        private static final long serialVersionUID = 2L;

        private String stfSeq;
        private String certificateType;
        private String certificateNumber;
        private String errorMessage;
    }
}
