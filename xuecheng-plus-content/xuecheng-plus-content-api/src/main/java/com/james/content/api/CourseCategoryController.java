package com.james.content.api;

import com.james.content.model.dto.CourseCategoryTreeDto;
import com.james.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程分类接口
 */
@RestController
@Slf4j
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService categoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return categoryService.queryTreeNodes("1");
    }
}
