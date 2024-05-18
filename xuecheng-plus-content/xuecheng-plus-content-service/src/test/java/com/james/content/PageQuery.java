package com.james.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.base.model.PageResult;
import com.james.content.mapper.CourseBaseMapper;
import com.james.content.model.dto.CourseBaseInfoDto;
import com.james.content.model.dto.QueryCourseParmsDto;
import com.james.content.model.po.CourseBase;
import com.james.content.service.CourseBaseInfoService;
import com.james.content.service.impl.CourseBaseInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class PageQuery {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Test
    public void testPage() {

    }
}
