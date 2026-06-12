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
import com.lrs.core.app.utils.MerchantContextHolder;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家后台 - 商品管理 Controller
 * 
 * <p>商家管理员可以管理自己店铺的商品，数据通过 merchant_id 隔离。
 * </p>
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Slf4j
@RestController
@RequestMapping("/merchant/product")
@Validated
public class MerchantProductController extends BaseController {

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizCategoryService bizCategoryService;

    @Resource
    private IBizMerchantService bizMerchantService;

    /**
     * 获取当前商家ID
     */
    private Long getMerchantId() {
        Long merchantId = MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new RuntimeException("请以商家身份登录");
        }
        return merchantId;
    }

    /**
     * 商家商品列表（分页）
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) ProductListDto dto) {
        Long merchantId = getMerchantId();
        
        LambdaQueryWrapper<BizProduct> query = new LambdaQueryWrapper<>();
        query.eq(BizProduct::getMerchantId, merchantId)
             .eq(BizProduct::getStatus, 1);

        if (dto != null && dto.getCategoryId() != null && dto.getCategoryId() > 0) {
            query.eq(BizProduct::getCategoryId, dto.getCategoryId());
        }
        if (dto != null && StrUtil.isNotBlank(dto.getKeyword())) {
            query.and(w -> w
                    .like(BizProduct::getProductName, dto.getKeyword())
                    .or()
                    .like(BizProduct::getProductTitle, dto.getKeyword()));
        }

        query.orderByDesc(BizProduct::getSortOrder)
             .orderByDesc(BizProduct::getId);

        Page<BizProduct> page = new Page<>(
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        Page<BizProduct> result = bizProductService.page(page, query);

        if (result.getRecords().isEmpty()) {
            return R.ok(result);
        }

        List<Long> productIds = result.getRecords().stream()
                .map(BizProduct::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.in(BizProductSku::getProductId, productIds)
                .eq(BizProductSku::getStatus, 1);
        Map<Long, List<BizProductSku>> skuGroupMap = bizProductSkuService.list(skuQuery).stream()
                .collect(Collectors.groupingBy(BizProductSku::getProductId));

        List<ProductVo> voList = result.getRecords().stream().map(product -> {
            ProductVo vo = new ProductVo();
            BeanUtil.copyProperties(product, vo);

            List<BizProductSku> skus = skuGroupMap.get(product.getId());
            if (skus != null && !skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(p -> p != null)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(p -> p != null)
                        .max(BigDecimal::compareTo)
                        .orElse(minPrice);
                int totalSales = skus.stream()
                        .mapToInt(s -> s.getSales() != null ? s.getSales() : 0)
                        .sum();

                vo.setMinPrice(minPrice)
                   .setMaxPrice(maxPrice)
                   .setSales(totalSales);
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> pageResult = new java.util.LinkedHashMap<>();
        pageResult.put("records", voList);
        pageResult.put("total", result.getTotal());
        pageResult.put("size", result.getSize());
        pageResult.put("current", result.getCurrent());
        pageResult.put("pages", result.getPages());

        return R.ok(pageResult);
    }

    /**
     * 获取商品详情
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody ProductDetailDto dto) {
        Long merchantId = getMerchantId();
        Long productId = dto.getProductId();
        
        BizProduct product = bizProductService.getById(productId);
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            return R.error("商品不存在");
        }

        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.eq(BizProductSku::getProductId, productId)
                .eq(BizProductSku::getStatus, 1)
                .orderByAsc(BizProductSku::getId);
        List<BizProductSku> skuList = bizProductSkuService.list(skuQuery);

        BizCategory category = bizCategoryService.getById(product.getCategoryId());

        ProductDetailVo vo = new ProductDetailVo();
        BeanUtil.copyProperties(product, vo);
        vo.setCategoryName(category != null ? category.getCategoryName() : "");

        if (product.getImageList() != null) {
            try {
                vo.setImageList(JSON.parseArray(product.getImageList(), String.class));
            } catch (Exception e) {
                vo.setImageList(Collections.singletonList(product.getMainImage()));
            }
        }

        int totalSales = skuList.stream()
                .mapToInt(s -> s.getSales() != null ? s.getSales() : 0)
                .sum();
        vo.setSales(totalSales);

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

    /**
     * 新增商品
     */
    @OperateLog(title = "商家后台-新增商品")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizProduct product) {
        Long merchantId = getMerchantId();
        product.setMerchantId(merchantId);
        product.setStatus((byte) 1);
        bizProductService.save(product);
        return R.ok(product.getId());
    }

    /**
     * 更新商品
     */
    @OperateLog(title = "商家后台-更新商品")
    @PostMapping("/update")
    @ResponseBody
    public R update(@RequestBody BizProduct product) {
        Long merchantId = getMerchantId();
        
        BizProduct exist = bizProductService.getById(product.getId());
        if (exist == null || !exist.getMerchantId().equals(merchantId)) {
            return R.error("商品不存在");
        }

        product.setMerchantId(merchantId);
        bizProductService.updateById(product);
        return R.ok();
    }

    /**
     * 删除商品（下架）
     */
    @OperateLog(title = "商家后台-删除商品")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody ProductDetailDto dto) {
        Long merchantId = getMerchantId();
        Long productId = dto.getProductId();
        
        BizProduct exist = bizProductService.getById(productId);
        if (exist == null || !exist.getMerchantId().equals(merchantId)) {
            return R.error("商品不存在");
        }

        exist.setStatus((byte) 0);
        bizProductService.updateById(exist);
        return R.ok();
    }
}