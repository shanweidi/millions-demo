package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-04-16 15:26
 **/
@Data
public class CustomerFieldDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fieldKey;
    private String classKey;
    /**
     * {@link TMapFieldDO#getOutFieldType()}
     */
    private String fieldType;

    private List<OptionValue> optionValueDtoList;

    @Data
    public static class OptionValue implements Serializable {
        private static final long serialVersionUID = 2L;
        /**
         * {@link TMapFieldValueDO#getInnerValue()}
         */
        private String optionCode;
        /**
         * {@link TMapFieldValueDO#getInnerValueDesc()}
         */
        private String optionValue;

        //新增时传""
        private String optionSeq;
    }
}
