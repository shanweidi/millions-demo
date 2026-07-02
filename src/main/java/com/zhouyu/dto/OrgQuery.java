package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-07-02 13:19
 **/
@Data
public class OrgQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> codes = Collections.emptyList();
    private List<String> ids = Collections.emptyList();

    private List<String> status = Collections.singletonList("active");

    private Long currentPage;
    //最大2000
    private Long pageSize;

    public static OrgQuery buildOrgQuery(int currentPage) {
        OrgQuery query = new OrgQuery();
        query.setCurrentPage((long) currentPage);
        query.setPageSize(2000L);
        return query;
    }
}
