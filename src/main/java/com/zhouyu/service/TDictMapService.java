package com.zhouyu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.TDictMapDO;
import com.zhouyu.mapper.TDictMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-05-11 10:38
 **/
@Service
public class TDictMapService extends ServiceImpl<TDictMapper, TDictMapDO> {

    public List<TDictMapDO> queryByCode(String code) {
        return this.baseMapper.selectList(Wrappers.<TDictMapDO>lambdaQuery()
                .eq(TDictMapDO::getCode, code));
    }
}
