package com.lrs.core.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.app.entity.BizMerchantAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商家结算账户 Mapper
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Mapper
public interface BizMerchantAccountMapper extends BaseMapper<BizMerchantAccount> {

    /**
     * 根据商家ID查询账户
     */
    BizMerchantAccount selectByMerchantId(@Param("merchantId") Long merchantId);
}
