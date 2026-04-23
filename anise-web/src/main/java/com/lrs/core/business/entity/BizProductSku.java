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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品规格库存表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_product_sku")
public class BizProductSku implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 规格名称
     */
    @TableField("spec_name")
    private String specName;

    /**
     * 规格属性
     */
    @TableField("spec_values")
    private String specValues;

    /**
     * 销售价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 划线原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 批发价
     */
    @TableField("wholesale_price")
    private BigDecimal wholesalePrice;

    /**
     * 库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 销量
     */
    @TableField("sales")
    private Integer sales;

    /**
     * 状态 0:禁用 1:启用
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

    /**
     * 逻辑删除 0:未删 1:已删
     */
    @TableField("is_deleted")
    private Byte isDeleted;


}
