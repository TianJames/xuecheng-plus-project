package com.james.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.mapper.CourseBaseMapper;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;
import com.james.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;

    /**
     * 课程分页查询
     * @param pageParms
     * @param queryCourseParmsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParms pageParms, QueryCourseParmsDto queryCourseParmsDto) {

        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(queryCourseParmsDto.getCourseName()),CourseBase::getName,queryCourseParmsDto.getCourseName());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParmsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParmsDto.getAuditStatus());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParmsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParmsDto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParms.getPageNo(), pageParms.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, lambdaQueryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParms.getPageNo(), pageParms.getPageSize());

        return courseBasePageResult;
    }
}
