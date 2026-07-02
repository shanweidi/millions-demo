package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-07-02 13:40
 **/
@Data
public class OrgResponse implements Serializable {
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
        private List<OrgInfo> records;
    }
    @Data
    public static class OrgInfo implements Serializable {
        private static final long serialVersionUID = 4L;
        private String id;
        private String code;
        private String name;
    }
}
