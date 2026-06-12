package com.lrs.core.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.entity.BizMerchantUser;

import java.util.List;

/**
 * 商家用户关联 Service
 * 
 * @author rstyro
 * @since 2026-06-12
 */
public interface IBizMerchantUserService extends IService<BizMerchantUser> {

    /**
     * 根据用户ID查询关联的商家列表
     */
    List<BizMerchantUser> getByUserId(Long userId);

    /**
     * 根据商家ID查询管理员列表
     */
    List<BizMerchantUser> getByMerchantId(Long merchantId);

    /**
     * 查询用户是否为商家管理员
     */
    BizMerchantUser getByUserIdAndMerchantId(Long userId, Long merchantId);

    /**
     * 获取用户关联的第一个商家ID（用户可能关联多个商家）
     */
    Long getFirstMerchantId(Long userId);
}
