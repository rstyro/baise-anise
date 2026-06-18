package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.core.app.dto.order.MerchantOrderCancelDto;
import com.lrs.core.app.dto.order.MerchantOrderDeliveryDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.entity.BizOrderLogistics;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.mapper.BizOrderMapper;
import com.lrs.core.business.mapper.BizOrderSubMapper;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderLogisticsService;
import com.lrs.core.business.service.IBizOrderSubService;
import com.lrs.core.system.dto.BaseDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商家订单子表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Service
public class BizOrderSubServiceImpl extends ServiceImpl<BizOrderSubMapper, BizOrderSub> implements IBizOrderSubService {

    @Resource
    private BizOrderMapper bizOrderMapper;

    @Resource
    private IBizOrderItemService bizOrderItemService;

    @Resource
    private IBizOrderLogisticsService bizOrderLogisticsService;

    @Override
    public Page<BizOrderSub> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizOrderSub::getSubNo, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizOrderSub::getId);
        return page(page, queryWrapper);
    }

    @Override
    public List<BizOrderSub> getByOrderId(Long orderId) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrderSub::getOrderId, orderId);
        return list(queryWrapper);
    }

    @Override
    public List<BizOrderSub> getByMerchantId(Long merchantId) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrderSub::getMerchantId, merchantId);
        return list(queryWrapper);
    }

    @Override
    public Map<Long, Long> countByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BizOrderSub::getOrderId, orderIds);
        return list(queryWrapper).stream()
                .collect(Collectors.groupingBy(BizOrderSub::getOrderId, LinkedHashMap::new, Collectors.counting()));
    }

    @Override
    public Object listMerchantOrders(Long merchantId, OrderQueryDto dto, int pageNo, int pageSize) {
        LambdaQueryWrapper<BizOrderSub> subQuery = new LambdaQueryWrapper<>();
        subQuery.eq(BizOrderSub::getMerchantId, merchantId)
                .orderByDesc(BizOrderSub::getId);
        if (dto != null && dto.getStatus() != null && dto.getStatus() > 0) {
            subQuery.eq(BizOrderSub::getDeliveryStatus, dto.getStatus());
        }

        Page<BizOrderSub> page = new Page<>(pageNo, pageSize);
        Page<BizOrderSub> result = page(page, subQuery);
        if (result.getRecords().isEmpty()) {
            return result;
        }

        List<Long> orderIds = result.getRecords().stream()
                .map(BizOrderSub::getOrderId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, BizOrder> orderMap = bizOrderMapper.selectBatchIds(orderIds).stream()
                .collect(Collectors.toMap(BizOrder::getId, order -> order));

        List<Map<String, Object>> voList = result.getRecords().stream().map(sub -> {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("subId", sub.getId());
            vo.put("subNo", sub.getSubNo());
            vo.put("orderId", sub.getOrderId());
            vo.put("orderNo", sub.getOrderNo());
            vo.put("merchantName", sub.getMerchantName());
            vo.put("itemAmount", sub.getItemAmount());
            vo.put("freightAmount", sub.getFreightAmount());
            vo.put("payAmount", sub.getPayAmount());
            vo.put("deliveryStatus", sub.getDeliveryStatus());
            vo.put("deliveryTime", sub.getDeliveryTime());
            vo.put("createTime", sub.getCreateTime());
            BizOrder order = orderMap.get(sub.getOrderId());
            if (order != null) {
                vo.put("orderStatus", order.getStatus());
                vo.put("addressSnapshot", order.getAddressSnapshot());
                vo.put("remark", order.getRemark());
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> pageResult = new LinkedHashMap<>();
        pageResult.put("records", voList);
        pageResult.put("total", result.getTotal());
        pageResult.put("current", result.getCurrent());
        pageResult.put("pages", result.getPages());
        return pageResult;
    }

    @Override
    public Map<String, Object> getMerchantSubDetail(Long merchantId, Long subId) {
        BizOrderSub sub = getMerchantSub(merchantId, subId);
        BizOrder order = bizOrderMapper.selectById(sub.getOrderId());
        List<BizOrderItem> items = bizOrderItemService.list(
                new LambdaQueryWrapper<BizOrderItem>().eq(BizOrderItem::getSubId, subId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sub", sub);
        result.put("order", order);
        result.put("items", items);
        result.put("logisticsList", bizOrderLogisticsService.getBySubOrderIds(Collections.singletonList(subId)));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliverMerchantSubOrder(Long merchantId, MerchantOrderDeliveryDto dto) {
        if (dto == null || dto.getSubId() == null) {
            throw new ServiceException("子订单ID不能为空");
        }
        if (!StringUtils.hasText(dto.getExpressCompany()) || !StringUtils.hasText(dto.getExpressNo())) {
            throw new ServiceException("请填写快递公司和快递单号");
        }

        BizOrderSub sub = getMerchantSub(merchantId, dto.getSubId());
        if (sub.getDeliveryStatus() != BizOrderSub.DELIVERY_STATUS_PENDING) {
            throw new ServiceException("当前子订单状态不可发货");
        }

        LocalDateTime now = LocalDateTime.now();
        sub.setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_SHIPPED);
        sub.setDeliveryTime(now);
        sub.setUpdateTime(now);
        updateById(sub);

        BizOrderLogistics logistics = new BizOrderLogistics()
                .setSubOrderId(sub.getId())
                .setLogisticsCompany(dto.getExpressCompany().trim())
                .setExpressCode(StringUtils.hasText(dto.getExpressCode()) ? dto.getExpressCode().trim() : null)
                .setTrackingNo(dto.getExpressNo().trim())
                .setStatus(BizOrderLogistics.STATUS_COLLECTED)
                .setLastTrackDetail("商家已发货")
                .setCreateTime(now)
                .setUpdateTime(now);
        bizOrderLogisticsService.save(logistics);

        List<BizOrderSub> allSubs = getByOrderId(sub.getOrderId());
        boolean allDelivered = allSubs.stream()
                .allMatch(item -> item.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_SHIPPED
                        || item.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_RECEIVED);
        if (allDelivered) {
            BizOrder order = bizOrderMapper.selectById(sub.getOrderId());
            if (order != null) {
                order.setStatus(BizOrder.STATUS_DELIVERED);
                order.setShipTime(now);
                order.setUpdateTime(now);
                bizOrderMapper.updateById(order);
            }
        }
    }

    @Override
    public void cancelMerchantSubOrder(Long merchantId, MerchantOrderCancelDto dto) {
        if (dto == null || dto.getSubId() == null) {
            throw new ServiceException("子订单ID不能为空");
        }
        BizOrderSub sub = getMerchantSub(merchantId, dto.getSubId());
        if (sub.getDeliveryStatus() != BizOrderSub.DELIVERY_STATUS_PENDING) {
            throw new ServiceException("当前子订单状态不可取消");
        }

        sub.setIsDeleted((byte) 1);
        sub.setMerchantRemark(dto.getCancelReason());
        updateById(sub);

        List<BizOrderSub> allSubs = getByOrderId(sub.getOrderId());
        boolean allCancelled = allSubs.stream().allMatch(item -> item.getIsDeleted() == 1);
        if (allCancelled) {
            BizOrder order = bizOrderMapper.selectById(sub.getOrderId());
            if (order != null) {
                order.setStatus(BizOrder.STATUS_CANCELLED);
                order.setCancelReason("所有商家已取消");
                order.setCancelTime(LocalDateTime.now());
                bizOrderMapper.updateById(order);
            }
        }
    }

    private BizOrderSub getMerchantSub(Long merchantId, Long subId) {
        if (subId == null) {
            throw new ServiceException("子订单ID不能为空");
        }
        BizOrderSub sub = getById(subId);
        if (sub == null || !merchantId.equals(sub.getMerchantId())) {
            throw new ServiceException("子订单不存在");
        }
        return sub;
    }

    @Override
    public boolean add(BizOrderSub item) {
        return save(item);
    }

    @Override
    public boolean edit(BizOrderSub item) {
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
