package com.zhouyu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.mapper.TMapFieldMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-04-17 16:24
 **/
@Service
public class TMapFieldService extends ServiceImpl<TMapFieldMapper, TMapFieldDO> {

    public List<TMapFieldDO> listAll() {
        return this.baseMapper.selectList(Wrappers.<TMapFieldDO>lambdaQuery()
                .eq(TMapFieldDO::getIsDelete,0));
    }

    public Boolean operateSync(Long id, String sql) {
        TMapFieldDO field = this.getById(id);
        //todo
        return Boolean.FALSE;
    }
}
