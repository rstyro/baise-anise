package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.mapper.BizAddressMapper;
import com.lrs.core.business.service.IBizAddressService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        public List<BizAddress> listAppAddresses(Long userId) {
            LambdaQueryWrapper<BizAddress> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BizAddress::getUserId, userId)
                    .orderByDesc(BizAddress::getIsDefault)
                    .orderByDesc(BizAddress::getId);
            return list(queryWrapper);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public BizAddress addAppAddress(Long userId, BizAddress address) {
            if (address == null) {
                throw new ServiceException("地址参数不能为空");
            }
            address.setUserId(userId);
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                clearDefaultAddresses(userId, null);
            }
            save(address);
            return address;
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void updateAppAddress(Long userId, BizAddress address) {
            if (address == null || address.getId() == null) {
                throw new ServiceException("地址ID不能为空");
            }
            BizAddress exist = getUserAddress(userId, address.getId());
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                clearDefaultAddresses(userId, exist.getId());
            }
            address.setUserId(userId);
            updateById(address);
        }

        @Override
        public void deleteAppAddress(Long userId, Long id) {
            getUserAddress(userId, id);
            removeById(id);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void setDefaultAppAddress(Long userId, Long id) {
            getUserAddress(userId, id);
            List<BizAddress> list = listAppAddresses(userId);
            list.forEach(address -> address.setIsDefault(address.getId().equals(id) ? (byte) 1 : (byte) 0));
            updateBatchById(list);
        }

        private BizAddress getUserAddress(Long userId, Long id) {
            if (id == null) {
                throw new ServiceException("地址ID不能为空");
            }
            BizAddress address = getById(id);
            if (address == null || !userId.equals(address.getUserId())) {
                throw new ServiceException("地址不存在");
            }
            return address;
        }

        private void clearDefaultAddresses(Long userId, Long excludeId) {
            LambdaQueryWrapper<BizAddress> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BizAddress::getUserId, userId);
            if (excludeId != null) {
                queryWrapper.ne(BizAddress::getId, excludeId);
            }
            List<BizAddress> list = list(queryWrapper);
            if (!list.isEmpty()) {
                list.forEach(address -> address.setIsDefault((byte) 0));
                updateBatchById(list);
            }
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
