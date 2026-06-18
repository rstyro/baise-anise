package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizOrderLogistics;
import com.lrs.core.business.service.IBizOrderLogisticsService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  子订单物流信息表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年6月18日
 */
@Controller
@RequestMapping("/business/bizOrderLogistics")
@RequiredArgsConstructor
public class BizOrderLogisticsController extends BaseController {

    private final IBizOrderLogisticsService bizOrderLogisticsService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizOrderLogistics:list","business:bizOrderLogistics:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_order_logistics";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizOrderLogistics:list","business:bizOrderLogistics:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizOrderLogistics> menuPage = bizOrderLogisticsService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizOrderLogistics:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizOrderLogistics item) {
        return R.ok(bizOrderLogisticsService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizOrderLogistics:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizOrderLogistics item) {
        return R.ok(bizOrderLogisticsService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizOrderLogistics:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizOrderLogisticsService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizOrderLogistics:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizOrderLogisticsService.batchDel(ids));
    }

}