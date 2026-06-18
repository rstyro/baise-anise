package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.address.AddressIdDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.service.IBizAddressService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序收货地址 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/app/address")
@Validated
public class AppAddressController extends BaseController {

    @Resource
    private IBizAddressService bizAddressService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) {
            throw new ServiceException("请先登录");
        }
        return user.getUserId();
    }

    /**
     * 地址列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        return R.ok(bizAddressService.listAppAddresses(getUserId()));
    }

    /**
     * 新增地址。
     */
    @OperateLog(title = "小程序-新增地址")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizAddress address) {
        return R.ok(bizAddressService.addAppAddress(getUserId(), address));
    }

    /**
     * 编辑地址。
     */
    @OperateLog(title = "小程序-编辑地址")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizAddress address) {
        bizAddressService.updateAppAddress(getUserId(), address);
        return R.ok();
    }

    /**
     * 删除地址。
     */
    @OperateLog(title = "小程序-删除地址")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody AddressIdDto dto) {
        bizAddressService.deleteAppAddress(getUserId(), dto.getId());
        return R.ok();
    }

    /**
     * 设置默认地址。
     */
    @PostMapping("/setDefault")
    @ResponseBody
    public R setDefault(@RequestBody AddressIdDto dto) {
        bizAddressService.setDefaultAppAddress(getUserId(), dto.getId());
        return R.ok();
    }
}
