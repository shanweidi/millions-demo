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
 * @since 2026-04-16 11:07
 **/
@TableName(value = "T_MAP_FIELD",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class TMapFieldDO implements Serializable {

    @TableId
    private Long id;

    private String innerTab;
    private String innerTableName;
    private String innerField;
    private String innerFieldKey;

    private String outTab;
    private String outField;
    private String outFieldClasskey;
    private String outFieldFieldkey;
    private String outTabDesc;
    private String outFieldDesc;
    //自定义字段类型（0字符串，23单选）
    private Integer outFieldType;

    // 1字符串；2固定字典；3字典
    private String fieldType;


    private String fieldDesc;
    private String tabDesc;

    private Integer isDelete;
    private Integer isSync;
    //编辑员工信息时，是否需要同步到第三方（0不需要；1需要同步）
    private Integer needSync;
    private Integer needTranslate;
}
