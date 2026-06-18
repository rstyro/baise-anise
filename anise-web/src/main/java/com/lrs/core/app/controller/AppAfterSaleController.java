package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.aftersale.AfterSaleIdDto;
import com.lrs.core.app.dto.aftersale.AfterSaleQueryDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.business.service.IBizAfterSaleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序售后 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/app/aftersale")
@Validated
public class AppAfterSaleController extends BaseController {

    @Resource
    private IBizAfterSaleService bizAfterSaleService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) {
            throw new ServiceException("请先登录");
        }
        return user.getUserId();
    }

    /**
     * 提交售后申请。
     */
    @OperateLog(title = "小程序-提交售后")
    @PostMapping("/apply")
    @ResponseBody
    public R apply(@RequestBody BizAfterSale afterSale) {
        return R.ok(bizAfterSaleService.applyAppAfterSale(getUserId(), afterSale));
    }

    /**
     * 售后列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) AfterSaleQueryDto dto) {
        return R.ok(bizAfterSaleService.listAppAfterSales(getUserId(), dto));
    }

    /**
     * 售后详情。
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody AfterSaleIdDto dto) {
        return R.ok(bizAfterSaleService.getAppAfterSaleDetail(getUserId(), dto.getId()));
    }
}
