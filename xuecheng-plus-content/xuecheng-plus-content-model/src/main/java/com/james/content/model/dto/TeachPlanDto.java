package com.james.content.model.dto;

import com.james.content.model.po.Teachplan;
import com.james.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;
@Data
public class TeachPlanDto extends Teachplan {

    private TeachplanMedia teachplanMedia;
    private List<TeachPlanDto> teachPlanTreeNodes;
}
