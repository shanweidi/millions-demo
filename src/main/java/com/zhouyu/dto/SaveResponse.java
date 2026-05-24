package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-20 15:44
 **/
@Data
public class SaveResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String returnCode;
    private String errorMsg;
    private List<SaveDetail> body;

    public static SaveResponse failed(String msg) {
        SaveResponse response = new SaveResponse();
        response.setErrorMsg(msg);
        response.setReturnCode("FAIL0001");
        return response;
    }

    @Data
    static class SaveDetail implements Serializable{
        private static final long serialVersionUID = 2L;

        private String sequence;
        private String successFlag;
        private String errorMessage;
    }
}
