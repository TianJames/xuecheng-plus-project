package com.james.content.service;

import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.model.dto.CourseCategoryTreeDto;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;

import java.util.List;

public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
