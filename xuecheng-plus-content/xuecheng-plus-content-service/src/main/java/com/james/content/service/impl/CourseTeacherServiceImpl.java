package com.james.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.james.content.mapper.CourseTeacherMapper;
import com.james.content.model.dto.CourseTeacherDto;
import com.james.content.model.po.CourseTeacher;
import com.james.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper teacherMapper;
    @Override
    public CourseTeacher list(Long companyId, Long id) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId,id);
        CourseTeacher courseTeacher = teacherMapper.selectOne(wrapper);
        return courseTeacher;
    }

    @Override
    public CourseTeacher add(CourseTeacherDto courseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacherDto,courseTeacher);
        teacherMapper.insert(courseTeacher);
        return courseTeacher;
    }
}
