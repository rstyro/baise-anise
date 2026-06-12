package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductDetailVo;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizCategory;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.service.IBizCategoryService;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序 - 商品浏览 Controller
 * <p>
 * 复用 business 模块的 Service/Mapper，返回面向小程序的精简 VO。
 * 所有接口仅返回上架（status=1）且未删除的商品。
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Slf4j
@RestController
@RequestMapping("/app/product")
@Validated
public class AppProductController extends BaseController {

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizCategoryService bizCategoryService;

    @Resource
    private IBizMerchantService bizMerchantService;

    /**
     * 小程序 - 商品分类列表
     */
    @PostMapping("/categoryList")
    @ResponseBody
    public R categoryList() {
        LambdaQueryWrapper<BizCategory> query = new LambdaQueryWrapper<>();
        query.orderByAsc(BizCategory::getSortOrder);
        List<BizCategory> list = bizCategoryService.list(query);
        return R.ok(list);
    }

    /**
     * 小程序 - 商品列表（分页 + 分类筛选 + 搜索）
     *
     * @param dto 查询参数
     */
    @OperateLog(title = "小程序-浏览商品列表")
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody ProductListDto dto) {
        // 查询条件：仅上架 + 未删除
        LambdaQueryWrapper<BizProduct> query = new LambdaQueryWrapper<>();
        query.eq(BizProduct::getStatus, 1);  // 仅上架

        // 分类筛选
        if (dto.getCategoryId() != null && dto.getCategoryId() > 0) {
            query.eq(BizProduct::getCategoryId, dto.getCategoryId());
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            query.and(w -> w
                    .like(BizProduct::getProductName, dto.getKeyword())
                    .or()
                    .like(BizProduct::getProductTitle, dto.getKeyword())
            );
        }

        query.orderByDesc(BizProduct::getSortOrder)
             .orderByDesc(BizProduct::getId);

        Page<BizProduct> page = new Page<>(
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        Page<BizProduct> result = bizProductService.page(page, query);

        // 如果没有数据直接返回
        if (result.getRecords().isEmpty()) {
            return R.ok(result);
        }

        // 批量查询 SKU：获取每个商品的最低/最高价格和总销量
        List<Long> productIds = result.getRecords().stream()
                .map(BizProduct::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.in(BizProductSku::getProductId, productIds)
                .eq(BizProductSku::getStatus, 1);
        List<BizProductSku> allSkus = bizProductSkuService.list(skuQuery);

        // 按 productId 分组，计算 min/max 价格和总销量
        Map<Long, List<BizProductSku>> skuGroupMap = allSkus.stream()
                .collect(Collectors.groupingBy(BizProductSku::getProductId));

        // 批量查询分类名
        Set<Long> categoryIds = result.getRecords().stream()
                .map(BizProduct::getCategoryId)
                .collect(Collectors.toSet());
        Map<Long, String> categoryMap = Collections.emptyMap();
        if (!categoryIds.isEmpty()) {
            categoryMap = bizCategoryService.listByIds(categoryIds).stream()
                    .collect(Collectors.toMap(BizCategory::getId, BizCategory::getCategoryName, (a, b) -> a));
        }

        // 组装 VO 列表
        Map<Long, String> finalCategoryMap = categoryMap;
        List<ProductVo> voList = result.getRecords().stream().map(product -> {
            ProductVo vo = new ProductVo()
                    .setId(product.getId())
                    .setProductName(product.getProductName())
                    .setProductTitle(product.getProductTitle())
                    .setMainImage(product.getMainImage())
                    .setOriginPlace(product.getOriginPlace())
                    .setIsSulfurFree(product.getIsSulfurFree() != null && product.getIsSulfurFree() == 1)
                    .setDryingLevel(product.getDryingLevel())
                    .setCategoryName(finalCategoryMap.getOrDefault(product.getCategoryId(), ""));

            // 填充价格和销量（从 SKU 聚合）
            List<BizProductSku> skus = skuGroupMap.get(product.getId());
            if (skus != null && !skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(Objects::nonNull)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(Objects::nonNull)
                        .max(BigDecimal::compareTo)
                        .orElse(minPrice);
                BigDecimal minOriginal = skus.stream()
                        .map(BizProductSku::getOriginalPrice)
                        .filter(Objects::nonNull)
                        .min(BigDecimal::compareTo)
                        .orElse(null);
                int totalSales = skus.stream()
                        .mapToInt(s -> s.getSales() != null ? s.getSales() : 0)
                        .sum();

                vo.setMinPrice(minPrice)
                   .setMaxPrice(maxPrice)
                   .setOriginalPrice(minOriginal)
                   .setSales(totalSales);
            }

            return vo;
        }).collect(Collectors.toList());

        // 构造返回的分页对象
        PageResultVo<ProductVo> pageResult = new PageResultVo<>();
        BeanUtil.copyProperties(result, pageResult);
        pageResult.setRecords(voList);

        return R.ok(pageResult);
    }

    /**
     * 小程序 - 商品详情（含 SKU 列表 + 商家信息）
     *
     * @param params 包含 productId
     */
    @OperateLog(title = "小程序-查看商品详情")
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody ProductDetailDto dto) {
        Long productId = dto.getProductId();
        if (productId == null) {
            return R.error("商品ID不能为空");
        }

        BizProduct product = bizProductService.getById(productId);
        if (product == null || product.getStatus() != 1) {
            return R.error("商品不存在或已下架");
        }

        // 查询 SKU 列表
        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.eq(BizProductSku::getProductId, productId)
                .eq(BizProductSku::getStatus, 1)
                .orderByAsc(BizProductSku::getId);
        List<BizProductSku> skuList = bizProductSkuService.list(skuQuery);

        // 查询分类
        BizCategory category = bizCategoryService.getById(product.getCategoryId());

        // 查询商家
        BizMerchant merchant = bizMerchantService.getById(product.getMerchantId());

        // 组装 VO
        ProductDetailVo vo = new ProductDetailVo()
                .setId(product.getId())
                .setProductName(product.getProductName())
                .setProductTitle(product.getProductTitle())
                .setMainImage(product.getMainImage())
                .setDescription(product.getDescription())
                .setOriginPlace(product.getOriginPlace())
                .setIsSulfurFree(product.getIsSulfurFree() != null && product.getIsSulfurFree() == 1)
                .setDryingLevel(product.getDryingLevel())
                .setPlantingProcess(product.getPlantingProcess())
                .setCategoryId(product.getCategoryId())
                .setCategoryName(category != null ? category.getCategoryName() : "")
                .setMerchantId(product.getMerchantId())
                .setMerchantName(merchant != null ? merchant.getMerchantName() : "")
                .setMerchantOriginPlace(merchant != null ? merchant.getOriginPlace() : "");

        // 解析 image_list JSON → List<String>
        if (product.getImageList() != null) {
            try {
                vo.setImageList(JSON.parseArray(product.getImageList(), String.class));
            } catch (Exception e) {
                vo.setImageList(Collections.singletonList(product.getMainImage()));
            }
        }

        // 总销量
        int totalSales = skuList.stream()
                .mapToInt(s -> s.getSales() != null ? s.getSales() : 0)
                .sum();
        vo.setSales(totalSales);

        // SKU 列表
        List<ProductDetailVo.SkuVo> skuVos = skuList.stream().map(sku -> new ProductDetailVo.SkuVo()
                .setId(sku.getId())
                .setSpecName(sku.getSpecName())
                .setSpecValues(sku.getSpecValues())
                .setPrice(sku.getPrice())
                .setOriginalPrice(sku.getOriginalPrice())
                .setStock(sku.getStock())
                .setSales(sku.getSales())
        ).collect(Collectors.toList());
        vo.setSkuList(skuVos);

        return R.ok(vo);
    }

}

