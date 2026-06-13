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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 商家结算表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_merchant_settlement")
public class BizMerchantSettlement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结算单号
     */
    @TableField("settle_no")
    private String settleNo;

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 结算周期类型 1:T+1 7:T+7 30:T+30
     */
    @TableField("period_type")
    private Byte periodType;

    /**
     * 结算周期开始
     */
    @TableField("period_start")
    private LocalDate periodStart;

    /**
     * 结算周期结束
     */
    @TableField("period_end")
    private LocalDate periodEnd;

    // ==================== 金额统计 ====================

    /**
     * 订单数量
     */
    @TableField("total_orders")
    private Integer totalOrders;

    /**
     * 收款总额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 平台抽成
     */
    @TableField("total_commission")
    private BigDecimal totalCommission;

    /**
     * 退款总额
     */
    @TableField("total_refund")
    private BigDecimal totalRefund;

    /**
     * 结算金额
     */
    @TableField("settle_amount")
    private BigDecimal settleAmount;

    // ==================== 状态流程 ====================

    /**
     * 状态 0:待确认 1:已确认 2:已打款 3:已到账
     */
    @TableField("status")
    private Byte status;

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    /**
     * 打款时间
     */
    @TableField("transfer_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transferTime;

    /**
     * 到账时间
     */
    @TableField("arrive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arriveTime;

    // ==================== 银行信息 ====================

    /**
     * 银行名称
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 银行账号
     */
    @TableField("bank_account")
    private String bankAccount;

    /**
     * 户名
     */
    @TableField("bank_username")
    private String bankUsername;

    // ==================== 备注 ====================

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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


    // ==================== 结算周期类型常量 ====================
    public static final byte PERIOD_TYPE_T1 = 1;   // T+1
    public static final byte PERIOD_TYPE_T7 = 7;   // T+7
    public static final byte PERIOD_TYPE_T30 = 30; // T+30

    // ==================== 结算状态常量 ====================
    public static final byte STATUS_PENDING = 0;    // 待确认
    public static final byte STATUS_CONFIRMED = 1;  // 已确认
    public static final byte STATUS_TRANSFERRED = 2; // 已打款
    public static final byte STATUS_ARRIVED = 3;    // 已到账
}
