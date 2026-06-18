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
 * 订单主表（用户视角的统一订单）
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_order")
public class BizOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    // ==================== 金额相关（聚合） ====================

    /**
     * 商品总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 运费总金额
     */
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠总金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付总金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    // ==================== 收货信息 ====================

    /**
     * 收货地址ID
     */
    @TableField("address_id")
    private Long addressId;

    /**
     * 收货地址快照
     */
    @TableField("address_snapshot")
    private String addressSnapshot;

    // ==================== 用户端可见状态 ====================

    /**
     * 订单状态 0:已取消 1:待支付 2:待发货 3:已发货 4:已收货 5:已完成 6:退款中 7:已退款
     */
    @TableField("status")
    private Byte status;

    /**
     * 支付方式 1:微信支付 2:余额支付 3:线下转账
     */
    @TableField("pay_type")
    private Byte payType;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    @TableField("pay_transaction_id")
    private String payTransactionId;

    // ==================== 物流相关（用户统一查看） ====================

    /**
     * 配送方式 1:快递 2:自提 3:送货上门
     */
    @TableField("delivery_type")
    private Byte deliveryType;

    /**
     * 发货时间（最后一笔发货时间）
     */
    @TableField("ship_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipTime;

    /**
     * 收货时间（最后一笔收货时间）
     */
    @TableField("receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

    // ==================== 用户操作 ====================

    /**
     * 用户备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

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


    // ==================== 订单状态常量 ====================
    public static final byte STATUS_CANCELLED = 0;      // 已取消
    public static final byte STATUS_PENDING_PAY = 1;    // 待支付
    public static final byte STATUS_PENDING_DELIVERY = 2; // 待发货
    public static final byte STATUS_DELIVERED = 3;      // 已发货
    public static final byte STATUS_RECEIVED = 4;       // 已收货
    public static final byte STATUS_COMPLETED = 5;      // 已完成
    public static final byte STATUS_REFUNDING = 6;      // 退款中
    public static final byte STATUS_REFUNDED = 7;        // 已退款

    // ==================== 支付方式常量 ====================
    public static final byte PAY_TYPE_WECHAT = 1;       // 微信支付
    public static final byte PAY_TYPE_BALANCE = 2;      // 余额支付
    public static final byte PAY_TYPE_OFFLINE = 3;      // 线下转账

    // ==================== 配送方式常量 ====================
    public static final byte DELIVERY_TYPE_EXPRESS = 1;  // 快递
    public static final byte DELIVERY_TYPE_SELF_PICKUP = 2; // 自提
    public static final byte DELIVERY_TYPE_HOME_DELIVERY = 3; // 送货上门
}
