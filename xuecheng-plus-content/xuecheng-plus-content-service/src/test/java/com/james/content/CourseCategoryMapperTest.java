package com.james.content;

import com.james.content.mapper.CourseCategoryMapper;
import com.james.content.model.dto.CourseCategoryTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class CourseCategoryMapperTest {
    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Test
    public void testCourseCategoryTreeNode() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes("1");
        courseCategoryTreeDtos.stream().forEach(System.out::println);

    }

    @Test
    public void testCourseCategoryTreeNode2() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.select();
        ArrayList<CourseCategoryTreeDto> list = new ArrayList<>();
        Map<String, CourseCategoryTreeDto> courseCategoryTreeDtoMap = courseCategoryTreeDtos.stream()
                .filter(item -> !item.getId().equals("1"))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        courseCategoryTreeDtos.stream()
                .filter(item->!item.getId().equals("1"))
                .forEach(item-> {
                    if (item.getParentid().equals("1")){
                        list.add(item);
                    }
                    CourseCategoryTreeDto courseCategoryTreeDto = courseCategoryTreeDtoMap.get(item.getParentid());
                    if (courseCategoryTreeDto != null){
                        if (courseCategoryTreeDto.getChildrenTreeNodes()==null){
                           courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        courseCategoryTreeDto.getChildrenTreeNodes().add(item);
                    }
                }
                );

    }

}
