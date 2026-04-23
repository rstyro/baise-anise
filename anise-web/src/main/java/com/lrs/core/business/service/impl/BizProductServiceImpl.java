package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.mapper.BizProductMapper;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


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
            // queryWrapper.like(BizProduct::getRemark, dto.getKeyword());
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

}
