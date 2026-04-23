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
 * 商家/农户表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_merchant")
public class BizMerchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联管理员用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 果园/商家名称
     */
    @TableField("merchant_name")
    private String merchantName;

    /**
     * 联系人
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 产地地址
     */
    @TableField("origin_place")
    private String originPlace;

    /**
     * 营业执照URL
     */
    @TableField("license_image")
    private String licenseImage;

    /**
     * 食品经营许可证URL
     */
    @TableField("food_license_image")
    private String foodLicenseImage;

    /**
     * 审核状态 0:待审核 1:通过 2:拒绝
     */
    @TableField("audit_status")
    private Byte auditStatus;

    /**
     * 审核备注
     */
    @TableField("audit_remark")
    private String auditRemark;

    /**
     * 平台抽成比例(%)
     */
    @TableField("commission_rate")
    private BigDecimal commissionRate;

    /**
     * 状态 0:禁用 1:正常
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
