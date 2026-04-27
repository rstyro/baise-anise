package com.lrs.core.base.dto;

import com.lrs.core.system.dto.BaseDto;
import lombok.Data;

@Data
public class VillageDto extends BaseDto {

    private String code;

    /**
     * 所属 乡级（乡镇、街道）
     */
    private String streetCode;

    /**
     * 所属 省级（省份、直辖市、自治区）
     */
    private String provinceCode;

    /**
     * 所属 地级（城市）
     */
    private String cityCode;

    /**
     * 所属 县级（区县）
     */
    private String areaCode;
}
