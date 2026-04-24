package com.lrs.core.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * <p>
 * 村级（村委会、居委会）-行政编码
 * </p>
 *
 * @author rstyro
 * @since 2026-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("base_village")
public class BaseVillage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 行政代码
     */
    @TableId(value = "code", type = IdType.AUTO)
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 所属 乡级（乡镇、街道）
     */
    @TableField("streetCode")
    private String streetCode;

    /**
     * 所属 省级（省份、直辖市、自治区）
     */
    @TableField("provinceCode")
    private String provinceCode;

    /**
     * 所属 地级（城市）
     */
    @TableField("cityCode")
    private String cityCode;

    /**
     * 所属 县级（区县）
     */
    @TableField("areaCode")
    private String areaCode;


}
