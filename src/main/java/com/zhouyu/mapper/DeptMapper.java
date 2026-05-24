package com.zhouyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouyu.domain.DeptDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-05-21 13:20
 **/
@Mapper
public interface DeptMapper extends BaseMapper<DeptDO> {

    @Select("select * from V_DEPT")
    List<DeptDO> all();
}
