package com.zhouyu.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-05-11 10:27
 **/
@TableName(value = "T_DICT_MAP",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class TDictMapDO implements Serializable {

    @TableId
    private Long id;

    private String code;
    private String key;
    private String value;
}
