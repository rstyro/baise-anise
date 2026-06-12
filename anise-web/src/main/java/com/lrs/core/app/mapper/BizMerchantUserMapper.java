package com.lrs.core.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.app.entity.BizMerchantUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家用户关联 Mapper
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Mapper
public interface BizMerchantUserMapper extends BaseMapper<BizMerchantUser> {

    /**
     * 根据用户ID查询关联的商家列表
     */
    List<BizMerchantUser> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据商家ID查询管理员列表
     */
    List<BizMerchantUser> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询用户是否为商家管理员
     */
    BizMerchantUser selectByUserIdAndMerchantId(@Param("userId") Long userId, @Param("merchantId") Long merchantId);
}
