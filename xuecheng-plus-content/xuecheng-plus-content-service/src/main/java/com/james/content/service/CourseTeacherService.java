package com.james.content.service;

import com.james.content.model.dto.CourseTeacherDto;
import com.james.content.model.po.CourseTeacher;

public interface CourseTeacherService {
    CourseTeacher list(Long companyId, Long id);
    CourseTeacher add(CourseTeacherDto courseTeacherDto);
}
