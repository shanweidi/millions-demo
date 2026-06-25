package com.zhouyu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-05-21 11:09
 **/
@TableName(value = "V_DEPT",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class DeptDO implements Serializable {

    @TableField("NAME")
    private String name;

    @TableField("TYPE")
    private String type;

    @TableField("PARENTORGCODE")
    private String parentOrgCode;
    @TableField("CODE")
    private String code;

    @TableField("DEPTNUMBER")
    private String deptNumber;
    @TableField("ORGNUMBER")
    private String orgNumber;
}
