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
 * 国内省市区数据表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("base_region")
public class BaseRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 行政代码
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 层级: 1-省, 2-市, 3-区/县, 4-乡镇/街道
     */
    @TableField("level")
    private Integer level;

    /**
     * 上级行政代码
     */
    @TableField("parent_code")
    private String parentCode;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
