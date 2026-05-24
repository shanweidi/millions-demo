package com.zhouyu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.DeptDO;
import com.zhouyu.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-05-21 13:21
 **/
@Service
public class DeptService extends ServiceImpl<DeptMapper, DeptDO> {

    public List<DeptDO> all() {
        List<DeptDO> allDept = this.baseMapper.all();
        allDept.stream().filter(e -> e.getParentOrgCode() == null)
                .findFirst()
                .ifPresent(e -> e.setParentOrgCode(""));
        return allDept;
    }
}
