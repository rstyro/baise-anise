package com.lrs.core.app.controller;

import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizBannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
        return R.ok(bizBannerService.listAppBanners());
    }

}
