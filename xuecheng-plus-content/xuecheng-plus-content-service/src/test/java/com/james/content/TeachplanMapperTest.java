package com.james.content;

import com.james.content.mapper.CourseCategoryMapper;
import com.james.content.mapper.TeachplanMapper;
import com.james.content.model.dto.CourseCategoryTreeDto;
import com.james.content.model.dto.TeachPlanDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class TeachplanMapperTest {
    @Autowired
    private TeachplanMapper teachplanMapper;

    @Test
    public void testCourseCategoryTreeNode(){
        List<TeachPlanDto> treeNodes = teachplanMapper.getTreeNodes(117L);
        System.out.println(treeNodes);

    }
}
