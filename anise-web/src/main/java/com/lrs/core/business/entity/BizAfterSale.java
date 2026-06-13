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
 * 售后申请表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_after_sale")
public class BizAfterSale implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 售后单号
     */
    @TableField("after_sale_no")
    private String afterSaleNo;

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

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    // ==================== 售后信息 ====================

    /**
     * 售后类型 1:仅退款 2:退货退款 3:换货 4:坏果赔付
     */
    @TableField("type")
    private Byte type;

    /**
     * 售后原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 凭证图片
     */
    @TableField("evidence_images")
    private String evidenceImages;

    /**
     * 用户备注
     */
    @TableField("user_remark")
    private String userRemark;

    // ==================== 退款金额 ====================

    /**
     * 申请退款金额
     */
    @TableField("apply_amount")
    private BigDecimal applyAmount;

    /**
     * 实际退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    // ==================== 退货物流 ====================

    /**
     * 退货快递公司
     */
    @TableField("return_express_company")
    private String returnExpressCompany;

    /**
     * 退货快递单号
     */
    @TableField("return_express_no")
    private String returnExpressNo;

    // ==================== 商家处理 ====================

    /**
     * 状态 0:待处理 1:商家审核中 2:已同意 3:已拒绝 4:已退款 5:已退货 6:已换货 7:已取消 8:平台介入
     */
    @TableField("status")
    private Byte status;

    /**
     * 处理结果
     */
    @TableField("handle_result")
    private String handleResult;

    /**
     * 处理备注
     */
    @TableField("handle_remark")
    private String handleRemark;

    /**
     * 处理时间
     */
    @TableField("handle_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    // ==================== 平台介入 ====================

    /**
     * 申诉状态 0:无申诉 1:申诉中 2:平台介入
     */
    @TableField("appeal_status")
    private Byte appealStatus;

    /**
     * 申诉原因
     */
    @TableField("appeal_reason")
    private String appealReason;

    /**
     * 申诉时间
     */
    @TableField("appeal_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appealTime;

    // ==================== 完成时间 ====================

    /**
     * 完成时间
     */
    @TableField("complete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

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


    // ==================== 售后类型常量 ====================
    public static final byte TYPE_REFUND_ONLY = 1;     // 仅退款
    public static final byte TYPE_REFUND_RETURN = 2;  // 退货退款
    public static final byte TYPE_EXCHANGE = 3;        // 换货
    public static final byte TYPE_DAMAGE = 4;          // 坏果赔付

    // ==================== 售后状态常量 ====================
    public static final byte STATUS_PENDING = 0;         // 待处理
    public static final byte STATUS_MERCHANT_REVIEW = 1; // 商家审核中
    public static final byte STATUS_AGREED = 2;         // 已同意
    public static final byte STATUS_REJECTED = 3;       // 已拒绝
    public static final byte STATUS_REFUNDED = 4;       // 已退款
    public static final byte STATUS_RETURNED = 5;       // 已退货
    public static final byte STATUS_EXCHANGED = 6;      // 已换货
    public static final byte STATUS_CANCELLED = 7;      // 已取消
    public static final byte STATUS_PLATFORM_INVOLVED = 8; // 平台介入

    // ==================== 申诉状态常量 ====================
    public static final byte APPEAL_STATUS_NONE = 0;      // 无申诉
    public static final byte APPEAL_STATUS_PENDING = 1;  // 申诉中
    public static final byte APPEAL_STATUS_INVOLVED = 2; // 平台介入
}
