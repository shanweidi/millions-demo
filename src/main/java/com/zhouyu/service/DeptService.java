package com.zhouyu.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyu.domain.DeptDO;
import com.zhouyu.dto.DeptVO;
import com.zhouyu.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author shanweidi
 * @since 2026-05-21 13:21
 **/
@Service
public class DeptService extends ServiceImpl<DeptMapper, DeptDO> {

    public List<DeptVO> all() {
        List<DeptDO> allDept = this.baseMapper.all();
        return allDept.stream().map(e -> {
            DeptVO vo = new DeptVO();
            vo.setCode(e.getCode());
            vo.setName(e.getName());
            vo.setType(e.getType());
            vo.setNumber(e.getDeptNumber());
            vo.setOrgNumber(e.getOrgNumber());
            vo.setParentOrgCode(e.getParentOrgCode() == null ? "" : e.getParentOrgCode());
            return vo;
        }).collect(Collectors.toList());
    }
}
