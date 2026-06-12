package com.lrs.core.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家用户关联实体
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Data
@TableName("biz_merchant_user")
public class BizMerchantUser {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 用户ID(app_user.id)
     */
    private Long userId;

    /**
     * 是否创始人 0:否 1:是
     */
    private Byte isOwner;

    /**
     * 状态 0:禁用 1:正常
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Byte isDeleted;
}
