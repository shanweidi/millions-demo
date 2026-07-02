package com.zhouyu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.mapper.TMapFileldValueMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author shanweidi
 * @since 2026-04-17 16:26
 **/
@Service
public class TMapFileldValueService extends ServiceImpl<TMapFileldValueMapper, TMapFileldValueDO> {

    public List<TMapFileldValueDO> queryByFieldId(Long fieldId) {
        return this.baseMapper.selectList(Wrappers.<TMapFileldValueDO>lambdaQuery()
                .eq(TMapFileldValueDO::getMapFieldId, fieldId));
    }

    public String queryOutValue(Long fieldId,Object inner) {
        return this.baseMapper.selectOne(Wrappers.<TMapFileldValueDO>lambdaQuery()
                .eq(TMapFileldValueDO::getMapFieldId, fieldId)
                .eq(TMapFileldValueDO::getInnerValue,inner)).getOutValue();
    }

    public List<Map<String, String>> selectMapsByCode(String code) {
        return this.baseMapper.selectMapsByCode(code);
    }

    public int removeByFieldId(Long fieldId) {
        return this.baseMapper.delete(Wrappers.<TMapFileldValueDO>lambdaQuery()
                .eq(TMapFileldValueDO::getMapFieldId, fieldId));
    }

    public TMapFileldValueDO selectNewest() {
        List<TMapFileldValueDO> entityList = this.baseMapper.selectList(Wrappers.<TMapFileldValueDO>lambdaQuery()
                .isNotNull(TMapFileldValueDO::getUpdateTime).orderByDesc(TMapFileldValueDO::getUpdateTime));
        return entityList.get(0);
    }
}
