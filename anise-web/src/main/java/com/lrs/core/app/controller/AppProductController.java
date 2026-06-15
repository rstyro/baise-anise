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
import com.lrs.core.business.entity.*;
import com.lrs.core.business.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private IBizProductSpuAttrService bizProductSpuAttrService;

    @Resource
    private IBizProductSkuAttrService bizProductSkuAttrService;

    @Resource
    private IBizAttributeService bizAttributeService;

    @Resource
    private IBizAttributeValueService bizAttributeValueService;

    @PostMapping("/categoryList")
    @ResponseBody
    public R categoryList() {
        LambdaQueryWrapper<BizCategory> query = new LambdaQueryWrapper<>();
        query.orderByAsc(BizCategory::getSortOrder);
        List<BizCategory> list = bizCategoryService.list(query);
        return R.ok(list);
    }

    @OperateLog(title = "小程序-浏览商品列表")
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody ProductListDto dto) {
        LambdaQueryWrapper<BizProduct> query = new LambdaQueryWrapper<>();
        query.eq(BizProduct::getStatus, 1);

        if (dto.getCategoryId() != null && dto.getCategoryId() > 0) {
            query.eq(BizProduct::getCategoryId, dto.getCategoryId());
        }

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

        if (result.getRecords().isEmpty()) {
            return R.ok(result);
        }

        List<Long> productIds = result.getRecords().stream()
                .map(BizProduct::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.in(BizProductSku::getProductId, productIds)
                .eq(BizProductSku::getStatus, 1);
        List<BizProductSku> allSkus = bizProductSkuService.list(skuQuery);

        Map<Long, List<BizProductSku>> skuGroupMap = allSkus.stream()
                .collect(Collectors.groupingBy(BizProductSku::getProductId));

        Set<Long> categoryIds = result.getRecords().stream()
                .map(BizProduct::getCategoryId)
                .collect(Collectors.toSet());
        Map<Long, String> categoryMap = Collections.emptyMap();
        if (!categoryIds.isEmpty()) {
            categoryMap = bizCategoryService.listByIds(categoryIds).stream()
                    .collect(Collectors.toMap(BizCategory::getId, BizCategory::getCategoryName, (a, b) -> a));
        }

        LambdaQueryWrapper<BizProductSpuAttr> spuAttrQuery = new LambdaQueryWrapper<>();
        spuAttrQuery.in(BizProductSpuAttr::getProductId, productIds);
        List<BizProductSpuAttr> spuAttrs = bizProductSpuAttrService.list(spuAttrQuery);

        Set<Long> attrIds = spuAttrs.stream().map(BizProductSpuAttr::getAttrId).collect(Collectors.toSet());
        Set<Long> attrValueIds = spuAttrs.stream().map(BizProductSpuAttr::getAttrValueId).collect(Collectors.toSet());

        Map<Long, BizAttribute> attributeMap = attrIds.isEmpty() ? Collections.emptyMap() :
                bizAttributeService.listByIds(attrIds).stream()
                        .collect(Collectors.toMap(BizAttribute::getId, a -> a));

        Map<Long, BizAttributeValue> attrValueMap = attrValueIds.isEmpty() ? Collections.emptyMap() :
                bizAttributeValueService.listByIds(attrValueIds).stream()
                        .collect(Collectors.toMap(BizAttributeValue::getId, a -> a));

        Map<Long, List<BizProductSpuAttr>> spuAttrGroupMap = spuAttrs.stream()
                .collect(Collectors.groupingBy(BizProductSpuAttr::getProductId));

        Map<Long, String> finalCategoryMap = categoryMap;
        List<ProductVo> voList = result.getRecords().stream().map(product -> {
            ProductVo vo = new ProductVo()
                    .setId(product.getId())
                    .setProductName(product.getProductName())
                    .setProductTitle(product.getProductTitle())
                    .setMainImage(product.getMainImage())
                    .setCategoryName(finalCategoryMap.getOrDefault(product.getCategoryId(), ""));

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

            List<BizProductSpuAttr> productSpuAttrs = spuAttrGroupMap.get(product.getId());
            if (productSpuAttrs != null && !productSpuAttrs.isEmpty()) {
                List<ProductVo.SpuAttrVo> spuAttrVos = productSpuAttrs.stream().map(spuAttr -> {
                    BizAttribute attr = attributeMap.get(spuAttr.getAttrId());
                    BizAttributeValue attrValue = attrValueMap.get(spuAttr.getAttrValueId());
                    return new ProductVo.SpuAttrVo()
                            .setAttrId(spuAttr.getAttrId())
                            .setAttrName(attr != null ? attr.getAttrName() : "")
                            .setAttrValueId(spuAttr.getAttrValueId())
                            .setAttrValue(attrValue != null ? attrValue.getValue() : "");
                }).collect(Collectors.toList());
                vo.setSpuAttrs(spuAttrVos);
            }

            return vo;
        }).collect(Collectors.toList());

        PageResultVo<ProductVo> pageResult = new PageResultVo<>();
        BeanUtil.copyProperties(result, pageResult);
        pageResult.setRecords(voList);

        return R.ok(pageResult);
    }

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

        LambdaQueryWrapper<BizProductSku> skuQuery = new LambdaQueryWrapper<>();
        skuQuery.eq(BizProductSku::getProductId, productId)
                .eq(BizProductSku::getStatus, 1)
                .orderByAsc(BizProductSku::getSortOrder)
                .orderByAsc(BizProductSku::getId);
        List<BizProductSku> skuList = bizProductSkuService.list(skuQuery);

        BizCategory category = bizCategoryService.getById(product.getCategoryId());

        BizMerchant merchant = bizMerchantService.getById(product.getMerchantId());

        LambdaQueryWrapper<BizProductSpuAttr> spuAttrQuery = new LambdaQueryWrapper<>();
        spuAttrQuery.eq(BizProductSpuAttr::getProductId, productId);
        List<BizProductSpuAttr> spuAttrs = bizProductSpuAttrService.list(spuAttrQuery);

        Set<Long> attrIds = new HashSet<>();
        Set<Long> attrValueIds = new HashSet<>();
        spuAttrs.forEach(spuAttr -> {
            attrIds.add(spuAttr.getAttrId());
            attrValueIds.add(spuAttr.getAttrValueId());
        });

        Map<Long, BizAttribute> attributeMap = attrIds.isEmpty() ? Collections.emptyMap() :
                bizAttributeService.listByIds(attrIds).stream()
                        .collect(Collectors.toMap(BizAttribute::getId, a -> a));

        Map<Long, BizAttributeValue> attrValueMap = attrValueIds.isEmpty() ? Collections.emptyMap() :
                bizAttributeValueService.listByIds(attrValueIds).stream()
                        .collect(Collectors.toMap(BizAttributeValue::getId, a -> a));

        List<Long> skuIds = skuList.stream().map(BizProductSku::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BizProductSkuAttr> skuAttrQuery = new LambdaQueryWrapper<>();
        skuAttrQuery.in(BizProductSkuAttr::getSkuId, skuIds);
        List<BizProductSkuAttr> skuAttrs = bizProductSkuAttrService.list(skuAttrQuery);

        skuAttrs.forEach(skuAttr -> {
            attrIds.add(skuAttr.getAttrId());
            attrValueIds.add(skuAttr.getAttrValueId());
        });

        if (!attrIds.isEmpty()) {
            bizAttributeService.listByIds(attrIds).forEach(a -> attributeMap.put(a.getId(), a));
        }
        if (!attrValueIds.isEmpty()) {
            bizAttributeValueService.listByIds(attrValueIds).forEach(a -> attrValueMap.put(a.getId(), a));
        }

        Map<Long, List<BizProductSkuAttr>> skuAttrGroupMap = skuAttrs.stream()
                .collect(Collectors.groupingBy(BizProductSkuAttr::getSkuId));

        ProductDetailVo vo = new ProductDetailVo()
                .setId(product.getId())
                .setProductName(product.getProductName())
                .setProductTitle(product.getProductTitle())
                .setMainImage(product.getMainImage())
                .setDescription(product.getDescription())
                .setCategoryId(product.getCategoryId())
                .setCategoryName(category != null ? category.getCategoryName() : "")
                .setMerchantId(product.getMerchantId())
                .setMerchantName(merchant != null ? merchant.getMerchantName() : "")
                .setMerchantOriginPlace(merchant != null ? merchant.getOriginPlace() : "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (product.getPreSaleStart() != null) {
            vo.setPreSaleStart(product.getPreSaleStart().format(formatter));
        }
        if (product.getPreSaleEnd() != null) {
            vo.setPreSaleEnd(product.getPreSaleEnd().format(formatter));
        }
        if (product.getEstimatedShipDate() != null) {
            vo.setEstimatedShipDate(product.getEstimatedShipDate().format(dateFormatter));
        }
        vo.setSeasonTag(product.getSeasonTag());

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

        List<ProductDetailVo.SpuAttrVo> spuAttrVos = spuAttrs.stream().map(spuAttr -> {
            BizAttribute attr = attributeMap.get(spuAttr.getAttrId());
            BizAttributeValue attrValue = attrValueMap.get(spuAttr.getAttrValueId());
            return new ProductDetailVo.SpuAttrVo()
                    .setAttrId(spuAttr.getAttrId())
                    .setAttrName(attr != null ? attr.getAttrName() : "")
                    .setAttrValueId(spuAttr.getAttrValueId())
                    .setAttrValue(attrValue != null ? attrValue.getValue() : "");
        }).collect(Collectors.toList());
        vo.setSpuAttrs(spuAttrVos);

        List<ProductDetailVo.SkuVo> skuVos = skuList.stream().map(sku -> {
            ProductDetailVo.SkuVo skuVo = new ProductDetailVo.SkuVo()
                    .setId(sku.getId())
                    .setSkuCode(sku.getSkuCode())
                    .setSaleUnit(sku.getSaleUnit())
                    .setUnitWeight(sku.getUnitWeight())
                    .setIsVariableWeight(sku.getIsVariableWeight() != null && sku.getIsVariableWeight() == 1)
                    .setMinQuantity(sku.getMinQuantity())
                    .setMaxQuantity(sku.getMaxQuantity())
                    .setQuantityStep(sku.getQuantityStep())
                    .setPrice(sku.getPrice())
                    .setOriginalPrice(sku.getOriginalPrice())
                    .setWholesalePrice(sku.getWholesalePrice())
                    .setStock(sku.getStock())
                    .setSales(sku.getSales());

            List<BizProductSkuAttr> skuAttrList = skuAttrGroupMap.get(sku.getId());
            if (skuAttrList != null && !skuAttrList.isEmpty()) {
                List<ProductDetailVo.SkuAttrVo> skuAttrVos = skuAttrList.stream().map(sa -> {
                    BizAttribute attr = attributeMap.get(sa.getAttrId());
                    BizAttributeValue attrValue = attrValueMap.get(sa.getAttrValueId());
                    return new ProductDetailVo.SkuAttrVo()
                            .setAttrId(sa.getAttrId())
                            .setAttrName(attr != null ? attr.getAttrName() : "")
                            .setAttrValueId(sa.getAttrValueId())
                            .setAttrValue(attrValue != null ? attrValue.getValue() : "");
                }).collect(Collectors.toList());
                skuVo.setSkuAttrs(skuAttrVos);
            }

            return skuVo;
        }).collect(Collectors.toList());
        vo.setSkuList(skuVos);

        return R.ok(vo);
    }

}
