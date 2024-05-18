package com.james.content.service;

import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.model.dto.AddCourseDto;
import com.james.content.model.dto.CourseBaseInfoDto;
import com.james.content.model.dto.EditCourseDto;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;

public interface CourseBaseInfoService {
    PageResult<CourseBase> queryCourseBaseList(PageParms pageParms, QueryCourseParmsDto queryCourseParmsDto);
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    void deleteCourse(Long id);
}
