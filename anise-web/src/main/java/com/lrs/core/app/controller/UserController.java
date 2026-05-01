package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.constant.Const;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.user.MiniLoginDto;
import com.lrs.core.app.dto.user.MiniRegisterDto;
import com.lrs.core.app.dto.user.UserAvatarDto;
import com.lrs.core.app.dto.user.UserInfoDto;
import com.lrs.core.app.service.IUserService;
import com.lrs.core.base.BaseController;
import com.lrs.core.util.StpKit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/app/user")
@Validated
public class UserController extends BaseController {

    @Resource
    private IUserService userService;

    /**
     * 是否登录
     */
    @GetMapping("/isLogin")
    public R isLogin(){
        return R.ok(StpKit.APP.isLogin());
    }


    /**
     * 发送验证码
     * @param dto
     * @return
     */
    @OperateLog(title = "发送验证码")
    @PostMapping("/sendCode")
    public R sendCode(@RequestBody MiniRegisterDto dto){
        return R.ok(userService.sendCode(dto));
    }

    /**
     * 注册
     * @param dto 参数
     * @return 返回token
     */
    @OperateLog(title = "注册")
    @PostMapping("/register")
    public R register(@RequestBody MiniRegisterDto dto){
        return R.ok(userService.register(dto));
    }

    /**
     * H5登录
     */
    @OperateLog(title = "H5登录")
    @PostMapping("/login")
    public R login(@RequestBody MiniRegisterDto dto){
        return R.ok(userService.login(dto));
    }

    /**
     * 小程序登录
     */
    @OperateLog(title = "小程序登录")
    @PostMapping("/appletLogin")
    public R login(@RequestBody MiniLoginDto dto){
        return R.ok(userService.appletLogin(dto));
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    public R logout(){
        StpKit.APP.logout();
        return R.ok();
    }

    /**
     * 测试用登录
     */
    @PostMapping("/testLogin")
    public R testLogin(){
        Long userId = 1L;
        StpKit.APP.login(userId);
        UserVo vo = new UserVo();
        vo.setUserId(userId);
        vo.setToken(StpKit.APP.getTokenValue());
        vo.setNickname("test");
        StpKit.APP.getSession().set(Const.SessionKey.SESSION_USER,vo);
        return R.ok(vo);
    }


    /**
     * 获取用户信息
     * @return
     * @throws Exception
     */
    @GetMapping("/getUserInfo")
    public R getUserInfo(){
        return R.ok(userService.getUserInfo());
    }

    /**
     * 上传头像
     */
    @OperateLog(title = "上传头像")
    @PostMapping(value = "/updateUserAvatar")
    public R<String> userAvatar(UserAvatarDto dto){
        return R.ok(userService.updateUserAvatar(dto));
    }

    /**
     * 更新用户信息
     * @param dto 参数
     * @return boolean
     */
    @OperateLog(title = "更新用户信息")
    @PostMapping(value = "/updateUserInfo")
    public R<Boolean> updateUserInfo(@RequestBody UserInfoDto dto){
        return R.ok(userService.updateUserInfo(dto));
    }


}
