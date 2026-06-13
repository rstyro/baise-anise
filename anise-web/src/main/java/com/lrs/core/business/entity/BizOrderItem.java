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
 * 订单商品明细表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
     * 订单编号（冗余）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 子订单ID
     */
    @TableField("sub_id")
    private Long subId;

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    // ==================== 商品信息 ====================

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品名称快照
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品主图快照
     */
    @TableField("product_image")
    private String productImage;

    // ==================== SKU信息 ====================

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 规格名称快照
     */
    @TableField("sku_name")
    private String skuName;

    /**
     * 规格明细快照
     */
    @TableField("sku_specs")
    private String skuSpecs;

    // ==================== 金额信息 ====================

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
     * 商品小计
     */
    @TableField("item_amount")
    private BigDecimal itemAmount;

    /**
     * 优惠分摊
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    // ==================== 售后相关 ====================

    /**
     * 退款状态 0:无退款 1:部分退款 2:全部退款
     */
    @TableField("refund_status")
    private Byte refundStatus;

    /**
     * 已退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    // ==================== 时间戳 ====================

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


    // ==================== 退款状态常量 ====================
    public static final byte REFUND_STATUS_NONE = 0;     // 无退款
    public static final byte REFUND_STATUS_PARTIAL = 1;  // 部分退款
    public static final byte REFUND_STATUS_FULL = 2;     // 全部退款
}
