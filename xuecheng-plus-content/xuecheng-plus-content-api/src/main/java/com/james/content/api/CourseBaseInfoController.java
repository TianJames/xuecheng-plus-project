package com.james.content.api;

import com.james.base.exception.ValidationGroups;
import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.model.dto.AddCourseDto;
import com.james.content.model.dto.CourseBaseInfoDto;
import com.james.content.model.dto.EditCourseDto;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;
import com.james.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
public class CourseBaseInfoController {

    @Resource
    private CourseBaseInfoService courseBaseInfoService;
    @PostMapping("/course/list")
    @ApiOperation("课程查询接口")
    public PageResult<CourseBase> list(PageParms pageParms,@RequestBody(required = false) QueryCourseParmsDto queryCourseParmsDto){
        log.info("{}",pageParms);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParms, queryCourseParmsDto);
        return courseBasePageResult;
    }

    @PostMapping("/course")
    @ApiOperation("新增课程")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.update.class) AddCourseDto addCourseDto){
        log.info("新增课程, {}",addCourseDto);
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);
    }

    @GetMapping("/course/{courseId}")
    @ApiOperation("根据课程id查询课程")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        log.info("根据id:{}查询课程信息",courseId);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }
    @PutMapping("/course")
    @ApiOperation("修改课程")
    public CourseBaseInfoDto modifyCourse(@RequestBody @Validated(value = ValidationGroups.update.class) EditCourseDto editCourseDto){
        log.info("修改课程信息{}",editCourseDto);
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }
    @DeleteMapping("/course/{id}")
    public void deleteCourse(@PathVariable Long id){
        log.info("删除课程：{}",id);
        courseBaseInfoService.deleteCourse(id);
    }
}
