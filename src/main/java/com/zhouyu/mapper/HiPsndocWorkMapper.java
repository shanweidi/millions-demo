package com.zhouyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhouyu.domain.HiPsndocWorkDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-31 09:05
 **/
@Mapper
public interface HiPsndocWorkMapper extends BaseMapper<HiPsndocWorkDO> {

    default List<HiPsndocWorkDO> selectListByPkPsndoc(String pkPsndoc) {
        return selectList(Wrappers.<HiPsndocWorkDO>lambdaQuery().eq(HiPsndocWorkDO::getPkPsndoc, pkPsndoc));
    }
}
