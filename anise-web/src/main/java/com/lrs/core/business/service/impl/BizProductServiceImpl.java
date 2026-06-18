package com.lrs.core.business.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductDetailVo;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.mapper.BizProductMapper;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
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

}
