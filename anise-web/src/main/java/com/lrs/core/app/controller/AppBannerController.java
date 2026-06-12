package com.lrs.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizBanner;
import com.lrs.core.business.service.IBizBannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序 - Banner轮播
 */
@RestController
@RequestMapping("/app/banner")
public class AppBannerController extends BaseController {

    @Resource
    private IBizBannerService bizBannerService;

    @PostMapping("/list")
    @ResponseBody
    public R list() {
        LambdaQueryWrapper<BizBanner> query = new LambdaQueryWrapper<>();
        query.eq(BizBanner::getStatus, (byte) 1).orderByAsc(BizBanner::getSortOrder);
        List<BizBanner> list = bizBannerService.list(query);
        return R.ok(list);
    }

}
