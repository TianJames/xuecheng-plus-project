package com.james.content.api;

import com.james.content.model.dto.CourseTeacherDto;
import com.james.content.model.po.CourseTeacher;
import com.james.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courseTeacher")
public class courseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;
    @GetMapping("/list/{id}")
    public CourseTeacher list(@PathVariable Long id){
        Long companyId = 1232141425L;
        return courseTeacherService.list(companyId, id);
    }

    @PostMapping()
    public CourseTeacher add(@RequestBody CourseTeacherDto courseTeacherDto){
        CourseTeacher courseTeacher = courseTeacherService.add(courseTeacherDto);
        return courseTeacher;
    }
}
