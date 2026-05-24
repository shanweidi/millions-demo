package com.zhouyu.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @date 2026-03-12 13:22
 **/
@TableName(value = "BD_PSNDOC",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class BdPsndocDO implements Serializable {
    @TableId
    private String pkPsndoc;

    //员工号
    private String code;
    //创建时间 2024-07-01 09:40:28
    private String creationtime;
    private String name;
    private String mobile;
    //状态 2正常
    private Integer enablestate;
    //1 男 2 女
    private Integer sex;
    private String birthdate;

    //身份证号
    private String id;

    //入职日期 2024-07-01
    private String glbdef5;
}
