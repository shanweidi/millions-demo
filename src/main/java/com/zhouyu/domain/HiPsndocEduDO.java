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
 * @since 2026-03-30 13:28
 **/
@TableName(value = "HI_PSNDOC_EDU",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class HiPsndocEduDO implements Serializable {

    @TableId
    private String pkPsndocSub;

    //创建时间 2024-07-01 09:40:28
    private String creationtime;

    private String begindate;
    private String enddate;

    private String pkPsndoc;
    private String major;
    private String school;
}
