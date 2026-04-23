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
import java.time.LocalDateTime;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_product")
public class BizProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品卖点
     */
    @TableField("product_title")
    private String productTitle;

    /**
     * 商品主图
     */
    @TableField("main_image")
    private String mainImage;

    /**
     * 轮播图列表
     */
    @TableField("image_list")
    private String imageList;

    /**
     * 商品详情
     */
    @TableField("description")
    private String description;

    /**
     * 产地
     */
    @TableField("origin_place")
    private String originPlace;

    /**
     * 是否无硫 0:否 1:是
     */
    @TableField("is_sulfur_free")
    private Byte isSulfurFree;

    /**
     * 干度
     */
    @TableField("drying_level")
    private String dryingLevel;

    /**
     * 种植工艺
     */
    @TableField("planting_process")
    private String plantingProcess;

    /**
     * 状态 0:下架 1:上架
     */
    @TableField("status")
    private Byte status;

    /**
     * 排序值
     */
    @TableField("sort_order")
    private Integer sortOrder;

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
