package com.james.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.james.base.exception.XueChengPlusException;
import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.content.mapper.*;
import com.james.content.model.dto.*;
import com.james.content.model.po.*;
import com.james.content.service.CourseBaseInfoService;
import com.james.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    /**
     * 课程分页查询
     *
     * @param pageParms
     * @param queryCourseParmsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParms pageParms, QueryCourseParmsDto queryCourseParmsDto) {

        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(queryCourseParmsDto.getCourseName()), CourseBase::getName, queryCourseParmsDto.getCourseName());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParmsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParmsDto.getAuditStatus());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParmsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParmsDto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParms.getPageNo(), pageParms.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, lambdaQueryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParms.getPageNo(), pageParms.getPageSize());

        return courseBasePageResult;
    }

    /**
     * @param companyId
     * @param addCourseDto
     * @return
     */
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //参数合法性校验

        //向课程基本信息表course_base写数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
            throw new RuntimeException("添加课程失败");
        }
        //向课程营销表course_market写数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        Long id = courseBase.getId();
        courseMarket.setId(id);
        saveCourseMarket(courseMarket);
        return getCourseBaseInfo(id);
    }

    //保存营销信息
    public int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new RuntimeException("收费规则为空");
        }
        //收费规则为收费
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                XueChengPlusException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarketObj == null) {
            return courseMarketMapper.insert(courseMarket);
        } else {
            BeanUtils.copyProperties(courseMarket, courseMarketObj);
            courseMarketObj.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }
    public CourseBaseInfoDto getCourseBaseInfo(Long id){
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategory.getName());
        CourseCategory courseCategory1 = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategory1.getName());
        return courseBaseInfoDto;
    }

    /**
     * 修改课程
     * @param companyId
     * @param editCourseDto
     * @return
     */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase==null){
            XueChengPlusException.cast("课程不存在");
        }
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        //修改课程基础信息
        BeanUtils.copyProperties(editCourseDto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0){
            XueChengPlusException.cast("修改失败");
        }
        //修改课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        int i1 = courseMarketMapper.updateById(courseMarket);
        if (i1 <=0){
            XueChengPlusException.cast("修改失败");
        }
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    @Override
    public void deleteCourse(Long id) {
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (Objects.equals(courseBase.getAuditStatus(), "202002")){
            //删除基本信息
            courseBaseMapper.deleteById(id);
            //删除营销信息
            courseMarketMapper.deleteById(id);
            //删除课程计划
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getCourseId,id);
            teachplanMapper.delete(wrapper);
            //课程计划关联信息
            LambdaQueryWrapper<TeachplanMedia> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(TeachplanMedia::getCourseId,id);
            teachplanMediaMapper.delete(wrapper1);
            //课程师资
            LambdaQueryWrapper<CourseTeacher> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(CourseTeacher::getCourseId,id);
            courseTeacherMapper.delete(wrapper2);
        }
    }

}
