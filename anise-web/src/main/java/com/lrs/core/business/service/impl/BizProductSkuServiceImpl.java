package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.utils.SkuCodeGenerator;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.mapper.BizProductSkuMapper;
import com.lrs.core.business.service.IBizProductSkuService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * <p>
 * 商品规格库存表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizProductSkuServiceImpl extends ServiceImpl<BizProductSkuMapper, BizProductSku> implements IBizProductSkuService {


    @Override
    public Page<BizProductSku> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizProductSku> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.like(BizProductSku::getSkuCode, dto.getKeyword());
        }
        if (!ObjectUtils.isEmpty(dto.getProductId())) {
            queryWrapper.eq(BizProductSku::getProductId, dto.getProductId());
        }
        queryWrapper.orderByDesc(BizProductSku::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(BizProductSku item) {
            // 如果skuCode为空，自动生成
            if (!StringUtils.hasText(item.getSkuCode())) {
                item.setSkuCode(generateSkuCode(item));
            }
            return save(item);
        }
        
        /**
         * 生成SKU编码（商家前缀固定为BS）
         */
        private String generateSkuCode(BizProductSku item) {
            if (item.getProductId() == null) {
                return SkuCodeGenerator.generateUnique();
            }
            
            // 获取同一商品下已有的SKU数量，确定序号
            LambdaQueryWrapper<BizProductSku> query = new LambdaQueryWrapper<>();
            query.eq(BizProductSku::getProductId, item.getProductId());
            int specIndex = (int) (count(query) + 1);
            
            return SkuCodeGenerator.generate(item.getProductId(), item.getSaleUnit(), specIndex);
        }
        
        @Override
        public boolean edit(BizProductSku item) {
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

}
