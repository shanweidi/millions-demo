package com.zhouyu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.TStfSeqDO;
import com.zhouyu.mapper.TStfSeqMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-05-07 15:50
 **/
@Service
public class TStfSeqService extends ServiceImpl<TStfSeqMapper, TStfSeqDO> {

    public TStfSeqDO getOneByPsndoc(String pkPsndoc) {
        return this.baseMapper.selectOne(Wrappers.<TStfSeqDO>lambdaQuery()
                .eq(TStfSeqDO::getPkPsndoc, pkPsndoc));
    }


    public List<TStfSeqDO> selectNeedSync() {
        return this.baseMapper.selectList(Wrappers.<TStfSeqDO>lambdaQuery()
                .eq(TStfSeqDO::getIsSync, 0));
    }
}
