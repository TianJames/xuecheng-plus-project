package com.james.content.service;

import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;

public interface CourseBaseInfoService {
    PageResult<CourseBase> queryCourseBaseList(PageParms pageParms, QueryCourseParmsDto queryCourseParmsDto);
}
