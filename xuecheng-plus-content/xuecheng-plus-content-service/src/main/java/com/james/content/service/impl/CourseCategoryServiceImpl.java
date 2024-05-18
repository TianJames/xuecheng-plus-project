package com.james.content.service.impl;

import com.james.content.mapper.CourseCategoryMapper;
import com.james.content.model.dto.CourseCategoryTreeDto;
import com.james.content.service.CourseCategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
//        //调用mapper 递归查询分类信息
//        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes(id);
//        //找到每个节点的子节点封装成List<CourseCategoryTreeDto>
//        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().
//                filter(item -> !id.equals(item.getId())).
//                collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
//        //定义一个list作为最终返回的list
//        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();
//        //从头遍历List<CourseCategoryTreeDto>，一边遍历一边找子节点放在父节点的childrenTreeNodes
//        courseCategoryTreeDtos.stream()
//                .filter(item -> !id.equals(item.getId()))
//                .forEach(item -> {
//                    if (item.getParentid().equals(id)) {
//                        courseCategoryList.add(item);
//                    }
//                    //找到每个子节点
//                    CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
//                    if (courseCategoryTreeDto != null) {
//                        if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
//                            courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
//                        }
//                        courseCategoryTreeDto.getChildrenTreeNodes().add(item);
//                    }
//                });
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.select();
        ArrayList<CourseCategoryTreeDto> list = new ArrayList<>();
        Map<String, CourseCategoryTreeDto> courseCategoryTreeDtoMap = courseCategoryTreeDtos.stream()
                .filter(item -> !item.getId().equals("1"))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        courseCategoryTreeDtos.stream()
                .filter(item->!item.getId().equals(id))
                .forEach(item-> {
                            if (item.getParentid().equals(id)){
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
        return list;
    }
}
