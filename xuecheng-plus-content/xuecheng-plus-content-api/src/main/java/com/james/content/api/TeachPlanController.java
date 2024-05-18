package com.james.content.api;

import com.james.base.model.Result;
import com.james.content.model.dto.SaveTeachplanDto;
import com.james.content.model.dto.TeachPlanDto;
import com.james.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class TeachPlanController {
    @Autowired
    private TeachPlanService teachPlanService;
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId){
        return teachPlanService.findTeachPlanTree(courseId);
    }

    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachPlanService.saveTeachplan(saveTeachplanDto);
        return;
    }
    @DeleteMapping("/teachplan/{id}")
    public Result deleteTeachplan(@PathVariable Long id){
        Result result = teachPlanService.deleteTeachplan(id);
        return result;
    }
    @PostMapping("/teachplan/{move}/{id}")
    public void orderByMove(@PathVariable String move,@PathVariable Long id){
        teachPlanService.orderByMove(move,id);
    }

}
