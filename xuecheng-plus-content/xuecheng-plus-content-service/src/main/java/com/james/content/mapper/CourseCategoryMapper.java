package com.james.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.james.content.model.dto.CourseCategoryTreeDto;
import com.james.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {


    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
    @Select("select * from course_category")
    List<CourseCategoryTreeDto> select();
}
