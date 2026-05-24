package com.zhouyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouyu.domain.TMapFileldValueDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 *
 * @author shanweidi
 * @since 2026-04-17 15:46
 **/
@Mapper
public interface TMapFileldValueMapper extends BaseMapper<TMapFileldValueDO> {

    @Select("SELECT t1.code,t1.name FROM bd_defdoc t1 LEFT JOIN bd_defdoclist t2 ON t1.pk_defdoclist = t2.pk_defdoclist WHERE t1.dr = 0 AND t1.enablestate = 2 AND t2.dr = 0 AND t2.code = #{code}")
    List<Map<String, String>> selectMapsByCode(@Param("code") String code);
}
