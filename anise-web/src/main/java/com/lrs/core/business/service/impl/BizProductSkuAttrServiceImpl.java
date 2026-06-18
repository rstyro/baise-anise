package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.core.business.dto.ProductSkuAttrSaveDto;
import com.lrs.core.business.entity.BizProductSkuAttr;
import com.lrs.core.business.mapper.BizProductSkuAttrMapper;
import com.lrs.core.business.service.IBizProductSkuAttrService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BizProductSkuAttrServiceImpl extends ServiceImpl<BizProductSkuAttrMapper, BizProductSkuAttr> implements IBizProductSkuAttrService {

    /**
     * 保存 SKU 属性配置。
     * SKU 允许关联多条属性，不能使用 selectOne/getOne 按 skuId 查询。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuAttrs(ProductSkuAttrSaveDto dto) {
        if (dto == null || dto.getSkuId() == null) {
            throw new ServiceException("SKU ID不能为空");
        }
        Long skuId = dto.getSkuId();
        Long productId = resolveProductId(dto);
        if (productId == null) {
            throw new ServiceException("商品ID不能为空");
        }

        LambdaQueryWrapper<BizProductSkuAttr> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(BizProductSkuAttr::getSkuId, skuId);
        remove(deleteQuery);

        List<ProductSkuAttrSaveDto.AttrItem> attrs = dto.getAttrs() == null ? Collections.emptyList() : dto.getAttrs();
        List<BizProductSkuAttr> skuAttrs = new ArrayList<>();
        for (ProductSkuAttrSaveDto.AttrItem attr : attrs) {
            if (attr == null || attr.getAttrId() == null || attr.getAttrValueId() == null) {
                continue;
            }
            skuAttrs.add(new BizProductSkuAttr()
                    .setSkuId(skuId)
                    .setProductId(productId)
                    .setAttrId(attr.getAttrId())
                    .setAttrValueId(attr.getAttrValueId()));
        }

        if (!skuAttrs.isEmpty()) {
            saveBatch(skuAttrs);
        }
    }

    private Long resolveProductId(ProductSkuAttrSaveDto dto) {
        if (dto.getProductId() != null) {
            return dto.getProductId();
        }
        List<BizProductSkuAttr> exists = list(new LambdaQueryWrapper<BizProductSkuAttr>()
                .eq(BizProductSkuAttr::getSkuId, dto.getSkuId())
                .last("LIMIT 1"));
        return exists.isEmpty() ? null : exists.get(0).getProductId();
    }
}
