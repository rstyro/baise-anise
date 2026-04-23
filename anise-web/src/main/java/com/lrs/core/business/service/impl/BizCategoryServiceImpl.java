package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizCategory;
import com.lrs.core.business.mapper.BizCategoryMapper;
import com.lrs.core.business.service.IBizCategoryService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizCategoryServiceImpl extends ServiceImpl<BizCategoryMapper, BizCategory> implements IBizCategoryService {


    @Override
    public Page<BizCategory> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizCategory::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizCategory::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(BizCategory item) {
            return save(item);
        }

        @Override
        public boolean edit(BizCategory item) {
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
