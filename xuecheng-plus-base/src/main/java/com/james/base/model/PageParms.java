package com.james.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 分页参数
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParms {
    private Long pageNo = 1L;
    private Long pageSize = 10L;
}
