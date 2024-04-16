package com.james.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程查询条件模型类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCourseParmsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
