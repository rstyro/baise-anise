package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.aftersale.AfterSaleIdDto;
import com.lrs.core.app.dto.aftersale.AfterSaleQueryDto;
import com.lrs.core.app.vo.AfterSaleListVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.service.IBizAfterSaleService;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 小程序 - 售后 Controller
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Slf4j
@RestController
@RequestMapping("/app/aftersale")
@Validated
public class AppAfterSaleController extends BaseController {

    @Resource private IBizAfterSaleService bizAfterSaleService;
    @Resource private IBizOrderService bizOrderService;
    @Resource private IBizOrderItemService bizOrderItemService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 提交售后申请
     */
    @OperateLog(title = "小程序-提交售后")
    @PostMapping("/apply")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R apply(@RequestBody BizAfterSale afterSale) {
        Long userId = getUserId();
        BizOrder order = bizOrderService.getById(afterSale.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) return R.error("订单不存在");
        if (order.getStatus() < 2 || order.getStatus() == 5) return R.error("当前订单状态不可申请售后");

        afterSale.setUserId(userId);
        afterSale.setAfterSaleNo("AS" + System.currentTimeMillis());
        afterSale.setStatus((byte) 0);
        afterSale.setCreateTime(LocalDateTime.now());
        afterSale.setUpdateTime(LocalDateTime.now());
        bizAfterSaleService.save(afterSale);

        // 更新订单为售后中
        order.setStatus((byte) 5);
        order.setUpdateTime(LocalDateTime.now());
        bizOrderService.updateById(order);

        return R.ok(afterSale);
    }

    /**
     * 售后列表
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody AfterSaleQueryDto dto) {
        Long userId = getUserId();
        Integer status = dto.getStatus();

        LambdaQueryWrapper<BizAfterSale> query = new LambdaQueryWrapper<>();
        query.eq(BizAfterSale::getUserId, userId).orderByDesc(BizAfterSale::getId);
        if (status != null) query.eq(BizAfterSale::getStatus, status);

        List<BizAfterSale> list = bizAfterSaleService.list(query);

        // 查询关联订单的商品信息
        if (!list.isEmpty()) {
            Set<Long> orderIds = list.stream().map(BizAfterSale::getOrderId).collect(Collectors.toSet());
            java.util.Map<Long, List<BizOrderItem>> itemMap = bizOrderItemService.list(
                    new LambdaQueryWrapper<BizOrderItem>()
                            .in(BizOrderItem::getOrderId, orderIds)
            ).stream().collect(Collectors.groupingBy(BizOrderItem::getOrderId));

            List<AfterSaleListVo> voList = list.stream().map(as -> {
                AfterSaleListVo vo = new AfterSaleListVo();
                BeanUtil.copyProperties(as, vo);

                List<BizOrderItem> items = itemMap.get(as.getOrderId());
                if (items != null && !items.isEmpty()) {
                    vo.setGoodsName(items.get(0).getProductName());
                    vo.setProductName(items.get(0).getProductName());
                    vo.setProductImage(items.get(0).getProductImage());
                }
                return vo;
            }).collect(Collectors.toList());
            return R.ok(voList);
        }
        return R.ok(Collections.emptyList());
    }

    /**
     * 售后详情
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody AfterSaleIdDto dto) {
        Long userId = getUserId();
        Long id = dto.getId();
        BizAfterSale afterSale = bizAfterSaleService.getById(id);
        if (afterSale == null || !afterSale.getUserId().equals(userId)) return R.error("售后单不存在");
        return R.ok(afterSale);
    }

}

