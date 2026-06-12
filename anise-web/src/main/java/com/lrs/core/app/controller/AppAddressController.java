package com.lrs.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.address.AddressIdDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.service.IBizAddressService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序 - 收货地址 Controller
 *
 * @author rstyro
 * @since 2026-06-11
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
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 地址列表
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        Long userId = getUserId();
        LambdaQueryWrapper<BizAddress> query = new LambdaQueryWrapper<>();
        query.eq(BizAddress::getUserId, userId).orderByDesc(BizAddress::getIsDefault).orderByDesc(BizAddress::getId);
        return R.ok(bizAddressService.list(query));
    }

    /**
     * 新增地址
     */
    @OperateLog(title = "小程序-新增地址")
    @PostMapping("/add")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R add(@RequestBody BizAddress address) {
        Long userId = getUserId();
        address.setUserId(userId);
        // 设为默认时，取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaQueryWrapper<BizAddress> query = new LambdaQueryWrapper<>();
            query.eq(BizAddress::getUserId, userId);
            List<BizAddress> list = bizAddressService.list(query);
            list.forEach(a -> a.setIsDefault((byte) 0));
            bizAddressService.updateBatchById(list);
        }
        bizAddressService.save(address);
        return R.ok(address);
    }

    /**
     * 编辑地址
     */
    @OperateLog(title = "小程序-编辑地址")
    @PostMapping("/edit")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R edit(@RequestBody BizAddress address) {
        Long userId = getUserId();
        BizAddress exist = bizAddressService.getById(address.getId());
        if (exist == null || !exist.getUserId().equals(userId)) return R.error("地址不存在");

        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaQueryWrapper<BizAddress> query = new LambdaQueryWrapper<>();
            query.eq(BizAddress::getUserId, userId).ne(BizAddress::getId, address.getId());
            List<BizAddress> list = bizAddressService.list(query);
            list.forEach(a -> a.setIsDefault((byte) 0));
            bizAddressService.updateBatchById(list);
        }
        bizAddressService.updateById(address);
        return R.ok();
    }

    /**
     * 删除地址
     */
    @OperateLog(title = "小程序-删除地址")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody AddressIdDto dto) {
        Long userId = getUserId();
        Long id = dto.getId();
        BizAddress exist = bizAddressService.getById(id);
        if (exist == null || !exist.getUserId().equals(userId)) return R.error("地址不存在");
        bizAddressService.removeById(id);
        return R.ok();
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/setDefault")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R setDefault(@RequestBody AddressIdDto dto) {
        Long userId = getUserId();
        Long id = dto.getId();
        LambdaQueryWrapper<BizAddress> query = new LambdaQueryWrapper<>();
        query.eq(BizAddress::getUserId, userId);
        List<BizAddress> list = bizAddressService.list(query);
        list.forEach(a -> a.setIsDefault(a.getId().equals(id) ? (byte) 1 : (byte) 0));
        bizAddressService.updateBatchById(list);
        return R.ok();
    }

}

