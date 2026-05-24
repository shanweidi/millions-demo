package com.zhouyu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 16:47
 **/
@Data
public class MappingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String xftSection;
    private String xftField;
    private String classKey;
    private String fieldKey;

    private String ncTab;
    private String ncField;

    private Boolean needValueTranslate;
    private List<Item> valueMapping;

    @Data
    public static class Item implements Serializable {
        private static final long serialVersionUID = 2L;

        private String xftValue;
        private String ncValue;
    }
}
