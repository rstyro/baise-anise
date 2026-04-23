package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.mapper.BizAddressMapper;
import com.lrs.core.business.service.IBizAddressService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 收货地址表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizAddressServiceImpl extends ServiceImpl<BizAddressMapper, BizAddress> implements IBizAddressService {


    @Override
    public Page<BizAddress> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizAddress> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizAddress::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizAddress::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(BizAddress item) {
            return save(item);
        }

        @Override
        public boolean edit(BizAddress item) {
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
