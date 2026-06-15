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
 * 属性值表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_attribute_value")
public class BizAttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性值ID，示例：10
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属属性ID（关联biz_attribute.id），示例：4（颜色属性）
     */
    @TableField("attr_id")
    private Long attrId;

    /**
     * 属性值名称，示例：红色、大果、一级
     */
    @TableField("value")
    private String value;

    /**
     * 排序，示例：1
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 扩展信息，例如关联图片或重量范围，示例：{"weight_range":"70-80mm", "image":"https://example.com/red.jpg"}
     */
    @TableField("extra_json")
    private String extraJson;

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
