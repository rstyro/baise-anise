package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.mapper.BizOrderItemMapper;
import com.lrs.core.business.service.IBizOrderItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizOrderItemServiceImpl extends ServiceImpl<BizOrderItemMapper, BizOrderItem> implements IBizOrderItemService {

}
