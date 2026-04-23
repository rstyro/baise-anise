package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  订单表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizOrder")
@RequiredArgsConstructor
public class BizOrderController extends BaseController {

    private final IBizOrderService bizOrderService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizOrder:list","business:bizOrder:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_order";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizOrder:list","business:bizOrder:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizOrder> menuPage = bizOrderService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizOrder:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizOrder item) {
        return R.ok(bizOrderService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizOrder:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizOrder item) {
        return R.ok(bizOrderService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizOrder:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizOrderService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizOrder:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizOrderService.batchDel(ids));
    }

}