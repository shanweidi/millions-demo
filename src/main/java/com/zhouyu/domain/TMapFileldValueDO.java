package com.zhouyu.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-04-16 11:22
 **/
@TableName(value = "T_MAP_FIELD_VALUE",autoResultMap = true)
@KeySequence(value = "FIELD_VALUE_SEQ")
@Data
@EqualsAndHashCode
@ToString
public class TMapFileldValueDO implements Serializable {

    @TableId
    private Long id;

    private Long mapFieldId;

    private String innerValue;
    private String innerValueDesc;
    private String outValue;

    private String createTime;
    private String updateTime;
}
