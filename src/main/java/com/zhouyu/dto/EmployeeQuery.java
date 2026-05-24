package com.zhouyu.dto;

import cn.hutool.core.map.MapUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author shanweidi
 * @since 2026-03-19 15:57
 **/
@Data
public class EmployeeQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer currentPage;
    private Integer pageSize;

    private List<QueryFilter> queryFilterList;
    private Map<String,Object> queryResultType;

    public EmployeeQuery(Integer currentPage, Integer pageSize, List<QueryFilter> queryFilterList) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.queryFilterList = queryFilterList;
        this.queryResultType = MapUtil.<String,Object>builder()
                .put("queryType","FIELD")
                .put("queryFieldList", Collections.singletonList("stfSeq")).build();
    }

    @Data
    static class QueryFilter implements Serializable {
        private static final long serialVersionUID = 1L;

        private String fieldKey;
        private String fieldQueryMethod;
        private String fieldValue;

        public QueryFilter(String fieldValue) {
            this.fieldKey = "certificateNumber";
            this.fieldQueryMethod = "EQUAL";
            this.fieldValue = fieldValue;
        }
    }

    public static EmployeeQuery buildDefault(String certificateNumber) {
        List<QueryFilter> filters = Collections.singletonList(new QueryFilter(certificateNumber));
        return new EmployeeQuery(1, 10, filters);
    }
}
