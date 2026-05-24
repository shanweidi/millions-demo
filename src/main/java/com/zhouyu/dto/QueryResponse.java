package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-20 10:56
 **/
@Data
public class QueryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String returnCode;
    private String errorMsg;
    private QueryDetail body;

    @Data
    public static class QueryDetail implements Serializable{
        private static final long serialVersionUID = 3L;

        private Integer currentPage;
        private Integer pageSize;
        private Integer totalSize;
        private List<EmployeeKey> records;
    }
    @Data
    public static class EmployeeKey implements Serializable {
        private static final long serialVersionUID = 4L;
        private String staffSeq;
    }
}
