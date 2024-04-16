package com.james.content.api;

import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;
import com.james.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;
    @PostMapping("/course/list")
    @ApiOperation("课程查询接口")
    public PageResult<CourseBase> list(PageParms pageParms,@RequestBody(required = false) QueryCourseParmsDto queryCourseParmsDto){
        log.info("{}",pageParms);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParms, queryCourseParmsDto);
        return courseBasePageResult;
    }
}
