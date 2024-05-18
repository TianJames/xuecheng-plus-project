package com.james.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.james.content.model.dto.TeachPlanDto;
import com.james.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    List<TeachPlanDto> getTreeNodes(Long courseId);
}
