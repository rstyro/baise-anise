package com.lrs.core.business.service.impl;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.OrderNumberGenerator;
import com.lrs.core.app.dto.order.OrderSubmitDto;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.app.vo.OrderSubmitResultVo;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.entity.BizAttribute;
import com.lrs.core.business.entity.BizAttributeValue;
import com.lrs.core.business.entity.BizCart;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.entity.BizProductSkuAttr;
import com.lrs.core.business.mapper.BizOrderMapper;
import com.lrs.core.business.service.IBizAddressService;
import com.lrs.core.business.service.IBizAttributeService;
import com.lrs.core.business.service.IBizAttributeValueService;
import com.lrs.core.business.service.IBizCartService;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.business.service.IBizOrderSubService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuAttrService;
import com.lrs.core.business.service.IBizProductSkuService;
import com.lrs.core.system.dto.BaseDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizOrderServiceImpl extends ServiceImpl<BizOrderMapper, BizOrder> implements IBizOrderService {

    @Resource
    private IBizOrderSubService bizOrderSubService;

    @Resource
    private IBizOrderItemService bizOrderItemService;

    @Resource
    private IBizCartService bizCartService;

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizAddressService bizAddressService;

    @Resource
    private IBizMerchantService bizMerchantService;

    @Resource
    private IBizProductSkuAttrService bizProductSkuAttrService;

    @Resource
    private IBizAttributeService bizAttributeService;

    @Resource
    private IBizAttributeValueService bizAttributeValueService;

    @Override
    public Page<BizOrder> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.like(BizOrder::getOrderNo, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizOrder::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BizOrder item) {
        return save(item);
    }

    @Override
    public boolean edit(BizOrder item) {
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

    @Override
    public Map<String, Object> countByStatus(Long userId) {
        return baseMapper.countByStatus(userId);
    }

    @Override
    public List<OrderGoodsVo> getGoodsList(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectOrderGoodsList(orderIds);
    }

    /**
     * 提交小程序订单。
     * 业务层负责订单创建和 MySQL 原子扣库存，重复提交由 Controller 的 @RepeatSubmit 统一拦截。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitResultVo submitAppOrder(OrderSubmitDto dto, Long userId) {
        if (userId == null) {
            throw new ServiceException("请先登录");
        }
        validateSubmitDto(dto);

        List<BizCart> orderItems = buildOrderCartItems(dto, userId);
        Map<Long, Integer> skuQuantityMap = mergeSkuQuantity(orderItems);
        Map<Long, BizProductSku> skuMap = loadSkuMap(skuQuantityMap.keySet());
        Map<Long, BizProduct> productMap = loadProductMap(orderItems, skuMap);

        BizAddress address = bizAddressService.getById(dto.getAddressId());
        if (address == null || !userId.equals(address.getUserId())) {
            throw new ServiceException("收货地址不存在");
        }

        validateOrderItems(orderItems, productMap, skuMap);
        Map<Long, BizMerchant> merchantMap = loadMerchantMap(productMap);
        Map<Long, List<BizCart>> cartByMerchant = groupByMerchant(orderItems, productMap);
        Map<Long, String> skuSpecsMap = buildSkuSpecsMap(skuQuantityMap.keySet());

        BigDecimal totalAmount = calculateTotalAmount(orderItems, skuMap);

        deductStock(skuQuantityMap, skuMap, productMap);

        LocalDateTime now = LocalDateTime.now();
        String orderNo = OrderNumberGenerator.nextId();
        BizOrder order = buildOrder(dto, userId, address, totalAmount, orderNo, now);
        save(order);

        List<BizOrderItem> orderItemList = new ArrayList<>();
        List<BizOrderSub> subList = buildSubOrdersAndItems(
                cartByMerchant, productMap, skuMap, merchantMap, skuSpecsMap, order, orderNo, now, orderItemList);

        bizOrderSubService.saveBatch(subList);
        bindSubIdsToOrderItems(subList, orderItemList);
        bizOrderItemService.saveBatch(orderItemList);

        if (dto.getSkuId() == null) {
            List<Long> cartIdsToRemove = orderItems.stream()
                    .map(BizCart::getId)
                    .filter(id -> id != null && id > 0)
                    .collect(Collectors.toList());
            if (!cartIdsToRemove.isEmpty()) {
                bizCartService.removeByIds(cartIdsToRemove);
            }
        }

        return new OrderSubmitResultVo()
                .setOrderId(order.getId())
                .setOrderNo(order.getOrderNo())
                .setPayAmount(totalAmount)
                .setMerchantCount(cartByMerchant.size());
    }

    private void validateSubmitDto(OrderSubmitDto dto) {
        if (dto == null) {
            throw new ServiceException("提交参数不能为空");
        }
        if (dto.getAddressId() == null) {
            throw new ServiceException("请选择收货地址");
        }
        boolean directBuy = dto.getSkuId() != null || dto.getQuantity() != null;
        if (directBuy) {
            if (dto.getSkuId() == null || dto.getQuantity() == null || dto.getQuantity() <= 0) {
                throw new ServiceException("请选择正确的商品规格和数量");
            }
        }
    }

    private List<BizCart> buildOrderCartItems(OrderSubmitDto dto, Long userId) {
        if (dto.getSkuId() != null) {
            BizProductSku sku = bizProductSkuService.getById(dto.getSkuId());
            if (sku == null) {
                throw new ServiceException("商品规格不存在");
            }

            BizCart tempCart = new BizCart();
            tempCart.setId(-1L);
            tempCart.setUserId(userId);
            tempCart.setProductId(sku.getProductId());
            tempCart.setSkuId(sku.getId());
            tempCart.setQuantity(dto.getQuantity());
            tempCart.setSelected(1);
            return Collections.singletonList(tempCart);
        }

        LambdaQueryWrapper<BizCart> cartQuery = new LambdaQueryWrapper<>();
        cartQuery.eq(BizCart::getUserId, userId)
                .eq(BizCart::getSelected, 1);
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            cartQuery.in(BizCart::getId, dto.getCartIds());
        }
        List<BizCart> cartItems = bizCartService.list(cartQuery);
        if (cartItems.isEmpty()) {
            throw new ServiceException("购物车为空");
        }
        return cartItems;
    }

    private Map<Long, Integer> mergeSkuQuantity(List<BizCart> orderItems) {
        Map<Long, Integer> skuQuantityMap = new TreeMap<>();
        for (BizCart item : orderItems) {
            if (item.getSkuId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new ServiceException("商品数量不正确");
            }
            skuQuantityMap.merge(item.getSkuId(), item.getQuantity(), Integer::sum);
        }
        return skuQuantityMap;
    }

    private Map<Long, BizProductSku> loadSkuMap(Set<Long> skuIds) {
        return bizProductSkuService.listByIds(skuIds).stream()
                .collect(Collectors.toMap(BizProductSku::getId, sku -> sku));
    }

    private Map<Long, BizProduct> loadProductMap(List<BizCart> orderItems, Map<Long, BizProductSku> skuMap) {
        Set<Long> productIds = new HashSet<>();
        for (BizCart item : orderItems) {
            if (item.getProductId() != null) {
                productIds.add(item.getProductId());
            }
            BizProductSku sku = skuMap.get(item.getSkuId());
            if (sku != null && sku.getProductId() != null) {
                productIds.add(sku.getProductId());
                item.setProductId(sku.getProductId());
            }
        }
        if (productIds.isEmpty()) {
            throw new ServiceException("商品不存在");
        }
        return bizProductService.listByIds(productIds).stream()
                .collect(Collectors.toMap(BizProduct::getId, product -> product));
    }

    private void validateOrderItems(List<BizCart> orderItems,
                                    Map<Long, BizProduct> productMap,
                                    Map<Long, BizProductSku> skuMap) {
        for (BizCart item : orderItems) {
            BizProductSku sku = skuMap.get(item.getSkuId());
            if (sku == null || sku.getStatus() == null || sku.getStatus() != 1) {
                throw new ServiceException("商品规格不存在或已下架");
            }
            if (sku.getPrice() == null) {
                throw new ServiceException("商品价格异常");
            }
            BizProduct product = productMap.get(sku.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new ServiceException("商品不存在或已下架");
            }
        }
    }

    private Map<Long, BizMerchant> loadMerchantMap(Map<Long, BizProduct> productMap) {
        Set<Long> merchantIds = productMap.values().stream()
                .map(BizProduct::getMerchantId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (merchantIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return bizMerchantService.listByIds(merchantIds).stream()
                .collect(Collectors.toMap(BizMerchant::getId, merchant -> merchant));
    }

    private Map<Long, List<BizCart>> groupByMerchant(List<BizCart> orderItems, Map<Long, BizProduct> productMap) {
        return orderItems.stream()
                .collect(Collectors.groupingBy(item -> {
                    BizProduct product = productMap.get(item.getProductId());
                    return product != null && product.getMerchantId() != null ? product.getMerchantId() : 0L;
                }, TreeMap::new, Collectors.toList()));
    }

    private BigDecimal calculateTotalAmount(List<BizCart> orderItems, Map<Long, BizProductSku> skuMap) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BizCart item : orderItems) {
            BizProductSku sku = skuMap.get(item.getSkuId());
            totalAmount = totalAmount.add(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return totalAmount;
    }

    private void deductStock(Map<Long, Integer> skuQuantityMap,
                             Map<Long, BizProductSku> skuMap,
                             Map<Long, BizProduct> productMap) {
        for (Map.Entry<Long, Integer> entry : skuQuantityMap.entrySet()) {
            Long skuId = entry.getKey();
            Integer quantity = entry.getValue();
            boolean success = bizProductSkuService.decreaseStock(skuId, BigDecimal.valueOf(quantity));
            if (!success) {
                BizProductSku sku = skuMap.get(skuId);
                BizProduct product = sku == null ? null : productMap.get(sku.getProductId());
                throw new ServiceException((product != null ? product.getProductName() : "商品") + " 库存不足");
            }
        }
    }

    private BizOrder buildOrder(OrderSubmitDto dto,
                                Long userId,
                                BizAddress address,
                                BigDecimal totalAmount,
                                String orderNo,
                                LocalDateTime now) {
        return new BizOrder()
                .setOrderNo(orderNo)
                .setUserId(userId)
                .setTotalAmount(totalAmount)
                .setFreightAmount(BigDecimal.ZERO)
                .setDiscountAmount(BigDecimal.ZERO)
                .setPayAmount(totalAmount)
                .setAddressId(address.getId())
                .setAddressSnapshot(JSON.toJSONString(address))
                .setRemark(dto.getRemark())
                .setStatus(BizOrder.STATUS_PENDING_PAY)
                .setDeliveryType(BizOrder.DELIVERY_TYPE_EXPRESS)
                .setCreateTime(now)
                .setUpdateTime(now)
                .setIsDeleted((byte) 0);
    }

    private List<BizOrderSub> buildSubOrdersAndItems(Map<Long, List<BizCart>> cartByMerchant,
                                                     Map<Long, BizProduct> productMap,
                                                     Map<Long, BizProductSku> skuMap,
                                                     Map<Long, BizMerchant> merchantMap,
                                                     Map<Long, String> skuSpecsMap,
                                                     BizOrder order,
                                                     String orderNo,
                                                     LocalDateTime now,
                                                     List<BizOrderItem> orderItemList) {
        List<BizOrderSub> subList = new ArrayList<>();
        for (Map.Entry<Long, List<BizCart>> entry : cartByMerchant.entrySet()) {
            Long merchantId = entry.getKey();
            List<BizCart> merchantCarts = entry.getValue();
            BigDecimal merchantTotal = calculateTotalAmount(merchantCarts, skuMap);
            BizMerchant merchant = merchantMap.get(merchantId);

            BizOrderSub sub = new BizOrderSub()
                    .setSubNo(OrderNumberGenerator.nextId())
                    .setOrderId(order.getId())
                    .setOrderNo(orderNo)
                    .setMerchantId(merchantId)
                    .setMerchantName(merchant != null ? merchant.getMerchantName() : "未知商家")
                    .setItemAmount(merchantTotal)
                    .setFreightAmount(BigDecimal.ZERO)
                    .setDiscountAmount(BigDecimal.ZERO)
                    .setMerchantDiscount(BigDecimal.ZERO)
                    .setPayAmount(merchantTotal)
                    .setCommissionRate(getCommissionRate(merchant))
                    .setCommissionAmount(BigDecimal.ZERO)
                    .setSettleAmount(merchantTotal)
                    .setSettleStatus(BizOrderSub.SETTLE_STATUS_UNSETTLED)
                    .setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_PENDING)
                    .setCreateTime(now)
                    .setUpdateTime(now)
                    .setIsDeleted((byte) 0);
            subList.add(sub);

            for (BizCart item : merchantCarts) {
                BizProduct product = productMap.get(item.getProductId());
                BizProductSku sku = skuMap.get(item.getSkuId());
                BigDecimal itemAmount = sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                orderItemList.add(new BizOrderItem()
                        .setOrderId(order.getId())
                        .setOrderNo(orderNo)
                        .setSubId(0L)
                        .setMerchantId(merchantId)
                        .setProductId(item.getProductId())
                        .setProductName(product != null ? product.getProductName() : "")
                        .setProductImage(product != null ? product.getMainImage() : "")
                        .setSkuId(item.getSkuId())
                        .setSkuName(sku.getSkuCode())
                        .setSkuSpecs(StringUtils.hasText(item.getSkuSpecs())
                                ? item.getSkuSpecs()
                                : skuSpecsMap.getOrDefault(item.getSkuId(), buildSkuSpecsJson(sku)))
                        .setPrice(sku.getPrice())
                        .setQuantity(item.getQuantity())
                        .setItemAmount(itemAmount)
                        .setDiscountAmount(BigDecimal.ZERO)
                        .setPayAmount(itemAmount)
                        .setRefundStatus(BizOrderItem.REFUND_STATUS_NONE)
                        .setRefundAmount(BigDecimal.ZERO)
                        .setCreateTime(now)
                        .setUpdateTime(now)
                        .setIsDeleted((byte) 0));
            }
        }
        return subList;
    }

    private BigDecimal getCommissionRate(BizMerchant merchant) {
        return merchant != null && merchant.getCommissionRate() != null
                ? merchant.getCommissionRate()
                : BigDecimal.ZERO;
    }

    private void bindSubIdsToOrderItems(List<BizOrderSub> subList, List<BizOrderItem> orderItemList) {
        Map<Long, Long> subIdMap = subList.stream()
                .collect(Collectors.toMap(BizOrderSub::getMerchantId, BizOrderSub::getId));
        orderItemList.forEach(item -> item.setSubId(subIdMap.get(item.getMerchantId())));
    }

    private Map<Long, String> buildSkuSpecsMap(Set<Long> skuIds) {
        Map<Long, String> result = new HashMap<>();
        if (skuIds == null || skuIds.isEmpty()) {
            return result;
        }

        LambdaQueryWrapper<BizProductSkuAttr> skuAttrQuery = new LambdaQueryWrapper<>();
        skuAttrQuery.in(BizProductSkuAttr::getSkuId, skuIds);
        List<BizProductSkuAttr> skuAttrs = bizProductSkuAttrService.list(skuAttrQuery);

        if (skuAttrs.isEmpty()) {
            return result;
        }

        Set<Long> attrIds = skuAttrs.stream().map(BizProductSkuAttr::getAttrId).collect(Collectors.toSet());
        Set<Long> attrValueIds = skuAttrs.stream().map(BizProductSkuAttr::getAttrValueId).collect(Collectors.toSet());

        Map<Long, BizAttribute> attributeMap = attrIds.isEmpty()
                ? Collections.emptyMap()
                : bizAttributeService.listByIds(attrIds).stream()
                .collect(Collectors.toMap(BizAttribute::getId, attr -> attr));
        Map<Long, BizAttributeValue> attrValueMap = attrValueIds.isEmpty()
                ? Collections.emptyMap()
                : bizAttributeValueService.listByIds(attrValueIds).stream()
                .collect(Collectors.toMap(BizAttributeValue::getId, value -> value));

        Map<Long, List<BizProductSkuAttr>> skuAttrGroupMap = skuAttrs.stream()
                .collect(Collectors.groupingBy(BizProductSkuAttr::getSkuId));

        for (Map.Entry<Long, List<BizProductSkuAttr>> entry : skuAttrGroupMap.entrySet()) {
            Map<String, String> specs = new LinkedHashMap<>();
            entry.getValue().stream()
                    .sorted(Comparator.comparing(BizProductSkuAttr::getId))
                    .forEach(attr -> {
                        BizAttribute attribute = attributeMap.get(attr.getAttrId());
                        BizAttributeValue attrValue = attrValueMap.get(attr.getAttrValueId());
                        if (attribute != null && attrValue != null) {
                            specs.put(attribute.getAttrName(), attrValue.getValue());
                        }
                    });
            if (!specs.isEmpty()) {
                result.put(entry.getKey(), JSON.toJSONString(specs));
            }
        }

        return result;
    }

    private String buildSkuSpecsJson(BizProductSku sku) {
        if (sku == null) {
            return null;
        }
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("销售单位", sku.getSaleUnit());
        if (sku.getUnitWeight() != null) {
            specs.put("单位重量(kg)", sku.getUnitWeight().toString());
        }
        if (sku.getIsVariableWeight() != null) {
            specs.put("是否浮动重量", sku.getIsVariableWeight() == 1 ? "是" : "否");
        }
        if (sku.getMinQuantity() != null) {
            specs.put("最小购买量", sku.getMinQuantity().toString());
        }
        if (sku.getMaxQuantity() != null) {
            specs.put("最大购买量", sku.getMaxQuantity().toString());
        }
        if (sku.getQuantityStep() != null) {
            specs.put("购买步进", sku.getQuantityStep().toString());
        }
        return JSON.toJSONString(specs);
    }

}
