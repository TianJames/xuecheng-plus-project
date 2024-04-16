package com.james.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.base.model.PageResult;
import com.james.content.mapper.CourseBaseMapper;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class test {

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);

        QueryCourseParmsDto queryCourseParmsDto = new QueryCourseParmsDto();
        queryCourseParmsDto.setCourseName("java");
        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(queryCourseParmsDto.getCourseName()),CourseBase::getName,queryCourseParmsDto.getCourseName());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParmsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParmsDto.getAuditStatus());
        Page<CourseBase> page = new Page<>(1, 2);
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, lambdaQueryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();
        PageResult<CourseBase> basePageResult = new PageResult<>(records, total, 1, 2);
        System.out.println(basePageResult);
    }
}
