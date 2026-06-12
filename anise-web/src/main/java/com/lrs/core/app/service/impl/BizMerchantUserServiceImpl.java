package com.lrs.core.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.app.entity.BizMerchantUser;
import com.lrs.core.app.mapper.BizMerchantUserMapper;
import com.lrs.core.app.service.IBizMerchantUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商家用户关联 Service 实现
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Service
public class BizMerchantUserServiceImpl extends ServiceImpl<BizMerchantUserMapper, BizMerchantUser> implements IBizMerchantUserService {

    @Resource
    private BizMerchantUserMapper bizMerchantUserMapper;

    @Override
    public List<BizMerchantUser> getByUserId(Long userId) {
        LambdaQueryWrapper<BizMerchantUser> query = new LambdaQueryWrapper<>();
        query.eq(BizMerchantUser::getUserId, userId)
             .eq(BizMerchantUser::getStatus, 1)
             .eq(BizMerchantUser::getIsDeleted, 0);
        return list(query);
    }

    @Override
    public List<BizMerchantUser> getByMerchantId(Long merchantId) {
        LambdaQueryWrapper<BizMerchantUser> query = new LambdaQueryWrapper<>();
        query.eq(BizMerchantUser::getMerchantId, merchantId)
             .eq(BizMerchantUser::getStatus, 1)
             .eq(BizMerchantUser::getIsDeleted, 0);
        return list(query);
    }

    @Override
    public BizMerchantUser getByUserIdAndMerchantId(Long userId, Long merchantId) {
        LambdaQueryWrapper<BizMerchantUser> query = new LambdaQueryWrapper<>();
        query.eq(BizMerchantUser::getUserId, userId)
             .eq(BizMerchantUser::getMerchantId, merchantId)
             .eq(BizMerchantUser::getStatus, 1)
             .eq(BizMerchantUser::getIsDeleted, 0);
        return getOne(query);
    }

    @Override
    public Long getFirstMerchantId(Long userId) {
        List<BizMerchantUser> list = getByUserId(userId);
        return list.isEmpty() ? null : list.get(0).getMerchantId();
    }
}
