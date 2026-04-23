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
 * 订单表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
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

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 订单类型 1:零售 2:批发
     */
    @TableField("order_type")
    private Byte orderType;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 运费
     */
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 支付方式 1:微信支付 2:线下转账
     */
    @TableField("pay_type")
    private Byte payType;

    /**
     * 支付状态 0:待支付 1:已支付 2:已退款
     */
    @TableField("pay_status")
    private Byte payStatus;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

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
     * 收货地址ID
     */
    @TableField("address_id")
    private Long addressId;

    /**
     * 地址快照
     */
    @TableField("address_snapshot")
    private String addressSnapshot;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 订单状态 0:取消 1:待支付 2:待发货 3:已发货 4:完成 5:售后中
     */
    @TableField("status")
    private Byte status;

    /**
     * 扩展字段
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

    /**
     * 逻辑删除 0:未删 1:已删
     */
    @TableField("is_deleted")
    private Byte isDeleted;


}
