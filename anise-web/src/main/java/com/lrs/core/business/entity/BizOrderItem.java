package com.lrs.core.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("biz_order_item")
public class BizOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 规格ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 商品名称快照
     */
    @TableField("product_name")
    private String productName;

    /**
     * 规格名称快照
     */
    @TableField("spec_name")
    private String specName;

    /**
     * 商品主图快照
     */
    @TableField("main_image")
    private String mainImage;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 小计金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0:未删 1:已删
     */
    @TableField("is_deleted")
    private Byte isDeleted;
}
