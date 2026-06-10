package com.zhouyu.controller;

import cn.hutool.core.map.MapUtil;
import com.zhouyu.domain.TMapFieldDO;
import com.zhouyu.domain.TMapFileldValueDO;
import com.zhouyu.dto.DeptVO;
import com.zhouyu.dto.MappingDTO;
import com.zhouyu.dto.Result;
import com.zhouyu.service.DeptService;
import com.zhouyu.service.TMapFieldService;
import com.zhouyu.service.TMapFileldValueService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author shanweidi
 * @since 2026-04-24 13:54
 **/
@RestController
@RequestMapping("/api")
public class MappingController {


    @Resource
    private TMapFieldService fieldService;

    @Resource
    private TMapFileldValueService valueService;

    @Resource
    private DeptService deptService;

    @GetMapping({"/v1/mapping"})
    public Result<List<Map<String, Object>>> queryMapping() {
        List<TMapFieldDO> domains = fieldService.listAll();
        List<MappingDTO> dtoList = domains.stream().map(e -> {
            MappingDTO dto = new MappingDTO();
            dto.setXftSection(e.getOutTab());
            dto.setXftField(e.getOutField());
            dto.setClassKey(e.getOutFieldClasskey());
            dto.setFieldKey(e.getOutFieldFieldkey());
            dto.setNcTab(e.getInnerTab());
            dto.setNcField(e.getInnerField());
            if (e.getNeedTranslate() == 0) {
                dto.setNeedValueTranslate(false);
            } else {
                dto.setNeedValueTranslate(true);
                List<TMapFileldValueDO> valueDOS = valueService.queryByFieldId(e.getId());
                List<MappingDTO.Item> itemList = valueDOS.stream().map(v -> {
                    MappingDTO.Item item = new MappingDTO.Item();
                    item.setXftValue(v.getOutValue());
                    item.setNcValue(v.getInnerValue());
                    return item;
                }).collect(Collectors.toList());
                dto.setValueMapping(itemList);
            }
            return dto;
        }).collect(Collectors.toList());
        Map<String, List<MappingDTO>> group = dtoList.stream().collect(Collectors.groupingBy(MappingDTO::getNcTab));
        List<Map<String, Object>> result = group.entrySet().stream().map(entry -> {
            return MapUtil.<String,Object>builder().put("nodeName",entry.getKey()).put("nodeList",entry.getValue()).build();
        }).collect(Collectors.toList());
        return Result.success(result);
    }


    @GetMapping({"/v1/mapping/version"})
    public Result<String> version() {
        TMapFileldValueDO newest = valueService.selectNewest();
        String result = newest.getUpdateTime().replace("-", "").replace(" ", "").replace(":", "");
        return Result.success(result);
    }

    @GetMapping({"/v1/org/list"})
    public Result<Map<String,List<DeptVO>>> orgList() {
        return Result.success(Collections.singletonMap("records",deptService.all()));
    }
}
