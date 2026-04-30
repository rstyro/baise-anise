package com.lrs.core.app.service;

import com.lrs.core.app.dto.user.MiniLoginDto;
import com.lrs.core.app.dto.user.MiniRegisterDto;
import com.lrs.core.app.dto.user.UserAvatarDto;
import com.lrs.core.app.dto.user.UserInfoDto;
import com.lrs.core.app.vo.AppUserVo;

/**
 * <p>
 * 小程序-用户第三方登录账号绑定 服务类
 * </p>
 *
 * @author rstyro
 * @since 2025-03-01
 */
public interface IUserService {

    /**
     * 小程序登录
     * @param dto
     * @return
     */
    AppUserVo appletLogin(MiniLoginDto dto);

    /**
     * 注册
     * @param dto 参数
     */
    AppUserVo register(MiniRegisterDto dto);
    AppUserVo login(MiniRegisterDto dto);

    boolean sendCode(MiniRegisterDto dto);

    AppUserVo getUserInfo();

    String updateUserAvatar(UserAvatarDto dto);

    boolean updateUserInfo(UserInfoDto dto);

}
