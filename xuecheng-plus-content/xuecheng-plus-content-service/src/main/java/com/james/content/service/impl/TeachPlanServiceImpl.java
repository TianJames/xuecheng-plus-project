package com.james.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.james.base.model.Result;
import com.james.content.mapper.TeachplanMapper;
import com.james.content.model.dto.SaveTeachplanDto;
import com.james.content.model.dto.TeachPlanDto;
import com.james.content.model.po.Teachplan;
import com.james.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TeachPlanServiceImpl implements TeachPlanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> treeNodes = teachplanMapper.getTreeNodes(courseId);
        return treeNodes;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //取出课程计划id ，如果为空：新增课程计划，如果不为空：修改课程计划
        Long id = saveTeachplanDto.getId();
        if (id==null){
            //新增课程计划
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定orderBy
            int count = getTeachplanCount(saveTeachplanDto.getCourseId(), saveTeachplanDto.getParentid());
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }else {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public Result deleteTeachplan(Long teachplanId) {
        //1. 删除大章节，大章节下有小章节时不允许删除。
        //2、删除大章节，大单节下没有小章节时可以正常删除。
        //3、删除小章节，同时将关联的信息进行删除。
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplanId);
        Integer i = teachplanMapper.selectCount(wrapper);
        log.debug("i:{}", i);
        if (i == 0) {
            //没查到以该id为parentId的数据，说明是小章节,或者是大章节下面没有小章节
            // 直接删除
            teachplanMapper.deleteById(teachplanId);
            return Result.success();
        }
            //说明该id为大章节数据，不能直接删除
        return Result.error("课程计划信息还有子级信息，无法操作");
    }

    @Override
    @Transactional
    public void orderByMove(String move,Long id) {
        Teachplan currentTeachplan = teachplanMapper.selectById(id);
        Integer orderby = currentTeachplan.getOrderby();
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        if (move.equals("moveup")){
            //上移目录
            if (orderby == 1){
                return;
            }
            wrapper.eq(Teachplan::getOrderby,orderby - 1)
                    .eq(Teachplan::getParentid,currentTeachplan.getParentid())
                    .eq(Teachplan::getCourseId,currentTeachplan.getCourseId());
            Teachplan switchTeachplan = teachplanMapper.selectOne(wrapper);
            currentTeachplan.setOrderby(orderby-1);
            switchTeachplan.setOrderby(orderby);
            teachplanMapper.updateById(currentTeachplan);
            teachplanMapper.updateById(switchTeachplan);
        }else if (move.equals("movedown")){
            //下移目录
            int count = getTeachplanCount(currentTeachplan.getCourseId(), currentTeachplan.getParentid());
            if (orderby == count){
                return;
            }
            wrapper.eq(Teachplan::getOrderby,orderby + 1)
                    .eq(Teachplan::getParentid,currentTeachplan.getParentid())
                    .eq(Teachplan::getCourseId,currentTeachplan.getCourseId());
            Teachplan switchTeachplan = teachplanMapper.selectOne(wrapper);
            currentTeachplan.setOrderby(orderby+1);
            switchTeachplan.setOrderby(orderby);
            teachplanMapper.updateById(currentTeachplan);
            teachplanMapper.updateById(switchTeachplan);
        }else {
            return;
        }
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        log.debug("同级别的数量：{}",count);
        return count;
    }
}
