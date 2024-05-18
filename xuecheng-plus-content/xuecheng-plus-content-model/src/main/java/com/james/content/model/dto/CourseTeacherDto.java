package com.james.content.model.dto;

import lombok.Data;

@Data
public class CourseTeacherDto {
    private Long courseId;
    private String teacherName;
    private String position;
    private String introduction;
}
