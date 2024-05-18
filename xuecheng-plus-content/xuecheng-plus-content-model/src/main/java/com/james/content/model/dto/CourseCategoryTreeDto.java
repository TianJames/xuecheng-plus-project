package com.james.content.model.dto;

import com.james.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
