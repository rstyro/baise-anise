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
 * 商家订单子表（商家视角的订单）
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_order_sub")
public class BizOrderSub implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 子订单编号
     */
    @TableField("sub_no")
    private String subNo;

    /**
     * 所属订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单编号（冗余便于查询）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 商家名称快照
     */
    @TableField("merchant_name")
    private String merchantName;

    // ==================== 金额相关（商家维度） ====================

    /**
     * 商品金额
     */
    @TableField("item_amount")
    private BigDecimal itemAmount;

    /**
     * 运费
     */
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠分摊
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 商家让利
     */
    @TableField("merchant_discount")
    private BigDecimal merchantDiscount;

    /**
     * 实付金额（商家应收）
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    // ==================== 结算相关 ====================

    /**
     * 抽成比例%
     */
    @TableField("commission_rate")
    private BigDecimal commissionRate;

    /**
     * 平台抽成
     */
    @TableField("commission_amount")
    private BigDecimal commissionAmount;

    /**
     * 结算金额（商家实收）
     */
    @TableField("settle_amount")
    private BigDecimal settleAmount;

    /**
     * 结算状态 0:未结算 1:已结算 2:结算中
     */
    @TableField("settle_status")
    private Byte settleStatus;

    /**
     * 结算时间
     */
    @TableField("settle_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settleTime;

    /**
     * 结算周期快照
     */
    @TableField("settle_period")
    private String settlePeriod;

    // ==================== 商家端发货状态（独立于主订单） ====================

    /**
     * 发货状态 0:待发货 1:已发货 2:已收货
     */
    @TableField("delivery_status")
    private Byte deliveryStatus;

    /**
     * 发货时间
     */
    @TableField("delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    @TableField("receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

    /**
     * 商家备注
     */
    @TableField("merchant_remark")
    private String merchantRemark;

    // ==================== 扩展字段 ====================

    /**
     * 扩展字段
     */
    @TableField("extra_json")
    private String extraJson;

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


    // ==================== 结算状态常量 ====================
    public static final byte SETTLE_STATUS_UNSETTLED = 0;  // 未结算
    public static final byte SETTLE_STATUS_SETTLED = 1;    // 已结算
    public static final byte SETTLE_STATUS_PROCESSING = 2; // 结算中

    // ==================== 发货状态常量 ====================
    public static final byte DELIVERY_STATUS_PENDING = 0;  // 待发货
    public static final byte DELIVERY_STATUS_SHIPPED = 1;  // 已发货
    public static final byte DELIVERY_STATUS_RECEIVED = 2; // 已收货
}
