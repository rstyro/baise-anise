package com.lrs.core.base.dto;

import com.lrs.core.system.dto.BaseDto;
import lombok.Data;

@Data
public class RegionDto extends BaseDto {
    // 层级
    private Integer level;
    // 父级编码
    private String parentCode;

    private String code;
}
