package com.zhouyu.domain;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-05-07 15:43
 **/
@TableName(value = "T_STF_SEQ",autoResultMap = true)
@KeySequence(value = "SEQ_T_STF_SEQ")
@Data
@EqualsAndHashCode
@ToString
public class TStfSeqDO implements Serializable {

    @TableId
    private Long id;

    private String stfSeq;

    private String pkPsndoc;

    private String certificateNumber;
    private String errMsg;
    private Integer isSync;
    private String createTime;
}
