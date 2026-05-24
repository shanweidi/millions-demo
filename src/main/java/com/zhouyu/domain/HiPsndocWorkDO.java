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
 * @since 2026-03-30 13:27
 **/
@TableName(value = "HI_PSNDOC_WORK",autoResultMap = true)
@Data
@EqualsAndHashCode
@ToString
public class HiPsndocWorkDO implements Serializable {

    @TableId
    private String pkPsndocSub;

    //创建时间 2024-07-01 09:40:28
    private String creationtime;

    private String begindate;
    private String enddate;

    private String pkPsndoc;
    //公司
    private String workcorp;
    //部门
    private String workdept;
    //岗位
    private String workpost;
}
