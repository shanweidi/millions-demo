package com.zhouyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhouyu.domain.HiPsndocEduDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-31 09:05
 **/
@Mapper
public interface HiPsndocEduMapper extends BaseMapper<HiPsndocEduDO> {

    default List<HiPsndocEduDO> selectListByPkPsndoc(String pkPsndoc) {
        return selectList(Wrappers.<HiPsndocEduDO>lambdaQuery().eq(HiPsndocEduDO::getPkPsndoc, pkPsndoc));
    }
}
