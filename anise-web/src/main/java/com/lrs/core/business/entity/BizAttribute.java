package com.lrs.core.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 属性定义表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_attribute")
public class BizAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性ID，示例：1
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 属性名称，示例：颜色、大小、品级、产地
     */
    @TableField("attr_name")
    private String attrName;

    /**
     * 属性类型：1-SPU属性（关键属性），2-SKU属性（销售属性），示例：2
     */
    @TableField("attr_type")
    private Byte attrType;

    /**
     * 输入类型：1-单选，2-多选，3-手填文本，示例：1
     */
    @TableField("input_type")
    private Byte inputType;

    /**
     * 是否必填：0-否，1-是，示例：1
     */
    @TableField("is_required")
    private Byte isRequired;

    /**
     * 是否支持前台筛选：0-否，1-是，示例：1
     */
    @TableField("is_searchable")
    private Byte isSearchable;

    /**
     * 排序（越小越靠前），示例：10
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用，示例：1
     */
    @TableField("status")
    private Byte status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
