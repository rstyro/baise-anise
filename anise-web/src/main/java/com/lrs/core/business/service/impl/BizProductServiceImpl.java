package com.lrs.core.business.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductDetailVo;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.business.entity.BizCategory;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.mapper.BizProductMapper;
import com.lrs.core.business.service.IBizCategoryService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuService;
import com.lrs.core.system.dto.BaseDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizProductServiceImpl extends ServiceImpl<BizProductMapper, BizProduct> implements IBizProductService {

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizCategoryService bizCategoryService;

    @Override
    public Page<BizProduct> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizProduct> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.like(BizProduct::getProductName, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizProduct::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BizProduct item) {
        return save(item);
    }

    @Override
    public boolean edit(BizProduct item) {
        return updateById(item);
    }

    @Override
    public boolean del(Long id) {
        return removeById(id);
    }

    @Override
    public boolean batchDel(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public PageResultVo<ProductVo> getAppProductList(ProductListDto dto, int pageNo, int pageSize) {
        Page<ProductVo> page = new Page<>(pageNo, pageSize);
        IPage<ProductVo> result = baseMapper.selectAppProductList(page, dto);

        if (result.getRecords().isEmpty()) {
            PageResultVo<ProductVo> pageResult = new PageResultVo<>();
            BeanUtil.copyProperties(result, pageResult);
            return pageResult;
        }

        List<Long> productIds = result.getRecords().stream()
                .map(ProductVo::getId)
                .collect(Collectors.toList());

        List<ProductVo.SpuAttrVo> spuAttrs = getBaseMapper().selectProductSpuAttrs(productIds);

        Map<Long, List<ProductVo.SpuAttrVo>> spuAttrGroupMap = spuAttrs.stream()
                .collect(Collectors.groupingBy(ProductVo.SpuAttrVo::getProductId));

        result.getRecords().forEach(vo -> {
            vo.setSpuAttrs(spuAttrGroupMap.getOrDefault(vo.getId(), Collections.emptyList()));
        });

        PageResultVo<ProductVo> pageResult = new PageResultVo<>();
        BeanUtil.copyProperties(result, pageResult);
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    @Override
    public ProductDetailVo getAppProductDetail(ProductDetailDto dto) {
        if (dto.getProductId() == null) {
            return null;
        }

        ProductDetailVo vo = getBaseMapper().selectAppProductDetail(dto);
        if (vo == null) {
            return null;
        }

        if (vo.getImageListStr() != null) {
            try {
                vo.setImageList(JSON.parseArray(vo.getImageListStr(), String.class));
            } catch (Exception e) {
                vo.setImageList(Collections.singletonList(vo.getMainImage()));
            }
        }

        List<ProductDetailVo.SkuVo> skuList = getBaseMapper().selectProductSkuList(dto.getProductId());
        vo.setSkuList(skuList);

        int totalSales = skuList.stream()
                .mapToInt(s -> s.getSales() != null ? s.getSales() : 0)
                .sum();
        vo.setSales(totalSales);

        List<ProductDetailVo.SpuAttrVo> spuAttrs = getBaseMapper().selectProductDetailSpuAttrs(dto.getProductId());
        vo.setSpuAttrs(spuAttrs);

        if (skuList != null && !skuList.isEmpty()) {
            List<Long> skuIds = skuList.stream().map(ProductDetailVo.SkuVo::getId).collect(Collectors.toList());
            List<ProductDetailVo.SkuAttrVo> skuAttrs = getBaseMapper().selectProductSkuAttrs(skuIds);

            Map<Long, List<ProductDetailVo.SkuAttrVo>> skuAttrGroupMap = skuAttrs.stream()
                    .collect(Collectors.groupingBy(ProductDetailVo.SkuAttrVo::getSkuId));

            skuList.forEach(sku -> {
                sku.setSkuAttrs(skuAttrGroupMap.getOrDefault(sku.getId(), Collections.emptyList()));
            });
        }

        return vo;
    }

    @Override
    public Object listMerchantProducts(Long merchantId, ProductListDto dto, int pageNo, int pageSize) {
        LambdaQueryWrapper<BizProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizProduct::getMerchantId, merchantId)
                .eq(BizProduct::getStatus, 1);
        if (dto != null && dto.getCategoryId() != null && dto.getCategoryId() > 0) {
            queryWrapper.eq(BizProduct::getCategoryId, dto.getCategoryId());
        }
        if (dto != null && StrUtil.isNotBlank(dto.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(BizProduct::getProductName, dto.getKeyword())
                    .or()
                    .like(BizProduct::getProductTitle, dto.getKeyword()));
        }
        queryWrapper.orderByDesc(BizProduct::getSortOrder)
                .orderByDesc(BizProduct::getId);

        Page<BizProduct> result = page(new Page<>(pageNo, pageSize), queryWrapper);
        if (result.getRecords().isEmpty()) {
            return result;
        }

        List<Long> productIds = result.getRecords().stream()
                .map(BizProduct::getId)
                .collect(Collectors.toList());
        Map<Long, List<BizProductSku>> skuGroupMap = bizProductSkuService.list(
                new LambdaQueryWrapper<BizProductSku>()
                        .in(BizProductSku::getProductId, productIds)
                        .eq(BizProductSku::getStatus, 1)
        ).stream().collect(Collectors.groupingBy(BizProductSku::getProductId));

        List<ProductVo> voList = result.getRecords().stream().map(product -> {
            ProductVo vo = new ProductVo();
            BeanUtil.copyProperties(product, vo);
            List<BizProductSku> skus = skuGroupMap.get(product.getId());
            if (skus != null && !skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(price -> price != null)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = skus.stream()
                        .map(BizProductSku::getPrice)
                        .filter(price -> price != null)
                        .max(BigDecimal::compareTo)
                        .orElse(minPrice);
                int totalSales = skus.stream()
                        .mapToInt(sku -> sku.getSales() != null ? sku.getSales() : 0)
                        .sum();
                vo.setMinPrice(minPrice)
                        .setMaxPrice(maxPrice)
                        .setSales(totalSales);
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> pageResult = new LinkedHashMap<>();
        pageResult.put("records", voList);
        pageResult.put("total", result.getTotal());
        pageResult.put("size", result.getSize());
        pageResult.put("current", result.getCurrent());
        pageResult.put("pages", result.getPages());
        return pageResult;
    }

    @Override
    public ProductDetailVo getMerchantProductDetail(Long merchantId, ProductDetailDto dto) {
        if (dto == null || dto.getProductId() == null) {
            throw new ServiceException("商品ID不能为空");
        }
        BizProduct product = getMerchantProduct(merchantId, dto.getProductId());
        List<BizProductSku> skuList = bizProductSkuService.list(
                new LambdaQueryWrapper<BizProductSku>()
                        .eq(BizProductSku::getProductId, product.getId())
                        .eq(BizProductSku::getStatus, 1)
                        .orderByAsc(BizProductSku::getId));
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
                .mapToInt(sku -> sku.getSales() != null ? sku.getSales() : 0)
                .sum();
        vo.setSales(totalSales);
        vo.setSkuList(skuList.stream().map(sku -> {
            ProductDetailVo.SkuVo skuVo = new ProductDetailVo.SkuVo();
            skuVo.setId(sku.getId());
            skuVo.setSkuCode(sku.getSkuCode());
            skuVo.setSaleUnit(sku.getSaleUnit());
            skuVo.setUnitWeight(sku.getUnitWeight());
            skuVo.setIsVariableWeight(sku.getIsVariableWeight() != null && sku.getIsVariableWeight() == 1);
            skuVo.setMinQuantity(sku.getMinQuantity());
            skuVo.setMaxQuantity(sku.getMaxQuantity());
            skuVo.setQuantityStep(sku.getQuantityStep());
            skuVo.setPrice(sku.getPrice());
            skuVo.setOriginalPrice(sku.getOriginalPrice());
            skuVo.setWholesalePrice(sku.getWholesalePrice());
            skuVo.setStock(sku.getStock());
            skuVo.setSales(sku.getSales());
            return skuVo;
        }).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public Long addMerchantProduct(Long merchantId, BizProduct product) {
        if (product == null) {
            throw new ServiceException("商品参数不能为空");
        }
        product.setMerchantId(merchantId);
        product.setStatus((byte) 1);
        save(product);
        return product.getId();
    }

    @Override
    public void updateMerchantProduct(Long merchantId, BizProduct product) {
        if (product == null || product.getId() == null) {
            throw new ServiceException("商品ID不能为空");
        }
        getMerchantProduct(merchantId, product.getId());
        product.setMerchantId(merchantId);
        updateById(product);
    }

    @Override
    public void deleteMerchantProduct(Long merchantId, Long productId) {
        BizProduct product = getMerchantProduct(merchantId, productId);
        product.setStatus((byte) 0);
        updateById(product);
    }

    private BizProduct getMerchantProduct(Long merchantId, Long productId) {
        if (productId == null) {
            throw new ServiceException("商品ID不能为空");
        }
        BizProduct product = getById(productId);
        if (product == null || !merchantId.equals(product.getMerchantId())) {
            throw new ServiceException("商品不存在");
        }
        return product;
    }

}
