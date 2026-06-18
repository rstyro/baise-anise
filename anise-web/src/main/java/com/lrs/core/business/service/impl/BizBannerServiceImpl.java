package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizBanner;
import com.lrs.core.business.mapper.BizBannerMapper;
import com.lrs.core.business.service.IBizBannerService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * Banner轮播图表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Service
public class BizBannerServiceImpl extends ServiceImpl<BizBannerMapper, BizBanner> implements IBizBannerService {


    @Override
    public Page<BizBanner> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizBanner> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizBanner::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizBanner::getId);
            return page(page, queryWrapper);
        }

        @Override
        public List<BizBanner> listAppBanners() {
            LambdaQueryWrapper<BizBanner> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BizBanner::getStatus, (byte) 1)
                    .orderByAsc(BizBanner::getSortOrder);
            return list(queryWrapper);
        }

        @Override
        public boolean add(BizBanner item) {
            return save(item);
        }

        @Override
        public boolean edit(BizBanner item) {
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
