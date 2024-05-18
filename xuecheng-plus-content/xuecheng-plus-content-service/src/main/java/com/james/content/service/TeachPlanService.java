package com.james.content.service;

import com.james.base.model.Result;
import com.james.content.model.dto.SaveTeachplanDto;
import com.james.content.model.dto.TeachPlanDto;

import java.util.List;

/**
 * 课程计划管理相关接口
 */
public interface TeachPlanService {
    List<TeachPlanDto> findTeachPlanTree(Long courseId);
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    Result deleteTeachplan(Long teachplanId);

    void orderByMove(String move , Long id);

}
