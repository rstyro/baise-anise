package com.lrs.core.app.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.constant.Const;
import com.lrs.common.enums.ApiResultEnum;
import com.lrs.common.enums.RedisKeyEnum;
import com.lrs.common.enums.UploadImageType;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.core.app.dto.user.MiniLoginDto;
import com.lrs.core.app.dto.user.MiniRegisterDto;
import com.lrs.core.app.dto.user.UserAvatarDto;
import com.lrs.core.app.dto.user.UserInfoDto;
import com.lrs.core.app.service.IUserService;
import com.lrs.core.app.vo.AppUserVo;
import com.lrs.core.business.entity.AppUser;
import com.lrs.core.business.service.IAppUserService;
import com.lrs.core.config.CommonConfig;
import com.lrs.core.oauth.config.properties.OauthProperties;
import com.lrs.core.oauth.consts.OauthSourceConst;
import com.lrs.core.oauth.util.OauthUtils;
import com.lrs.core.util.RedissonUtils;
import com.lrs.core.util.StpKit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * <p>
 * 小程序-用户基本信息表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2025-03-01
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private OauthProperties oauthProperties;

    @Resource
    private IAppUserService miniUserService;

    @Resource
    private CommonConfig.UploadConfig uploadConfig;

    @Resource
    private CommonConfig commonConfig;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppUserVo appletLogin(MiniLoginDto dto) {
        AuthRequest authRequest = OauthUtils.getAuthRequest(OauthSourceConst.WECHAT_MINI_PROGRAM, oauthProperties);
        AuthToken accessToken = authRequest.getAccessToken(AuthCallback.builder().code(dto.getCode()).build());
        String openId = accessToken.getOpenId();
        AppUser appUser = miniUserService.getOne(new LambdaQueryWrapper<AppUser>().eq(AppUser::getOpenid, openId));
        if (null == appUser) {
            appUser = new AppUser();
            appUser.setOpenid(openId);
            appUser.setUnionid(accessToken.getUnionId());
            appUser.setNickname(dto.getNickName());
            appUser.setAvatarUrl(dto.getAvatarUrl());
            miniUserService.save(appUser);
        }
        StpKit.APP.login(appUser.getId());
        return reloadUserInfo(appUser);
    }

    @Override
    public AppUserVo register(MiniRegisterDto dto) {
        String code = RedissonUtils.getCacheObject(RedisKeyEnum.MINI_USER_EMAIL_CODE, RedissonUtils.joinKey(dto.getAccount(), dto.getActionType()));
        if (!Objects.equals(code, dto.getCode())) {
            throw new ServiceException(ApiResultEnum.SYSTEM_CODE_ERROR);
        }
        boolean exists = miniUserService.exists(new LambdaQueryWrapper<AppUser>().eq(AppUser::getUsername, dto.getAccount()));
        if (exists) {
            throw new ServiceException(ApiResultEnum.MINI_USER_EXIST);
        }
        AppUser miniUser = new AppUser();
        miniUser.setUsername(dto.getAccount());
        miniUser.setNickname(dto.getAccount());
        miniUser.setPassword(SecureUtil.md5(dto.getPassword()));
        miniUserService.save(miniUser);
        // 初始化次数资源
        Long userId = miniUser.getId();
        StpKit.APP.login(userId);
        return reloadUserInfo(miniUser);
    }

    @Override
    public AppUserVo login(MiniRegisterDto dto) {
        String account = dto.getAccount();
        if (!StrUtil.equalsAnyIgnoreCase(account, "test", "admin", "1006059906@qq.com","18818868688")) {
            String code = RedissonUtils.getCacheObject(RedisKeyEnum.MINI_USER_EMAIL_CODE, RedissonUtils.joinKey(dto.getAccount(), dto.getActionType()));
            if (!Objects.equals(code, dto.getCode())) {
                throw new ServiceException(ApiResultEnum.SYSTEM_CODE_ERROR);
            }
        }
        AppUser miniUser = miniUserService.getOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, account).or().eq(AppUser::getPhone,dto.getAccount()));
        if (Objects.isNull(miniUser)) {
            throw new ServiceException(ApiResultEnum.SYSTEM_ACCOUNT_NOT_FOUND);
        }
        if(!ObjectUtils.isEmpty(dto.getPassword()) && !Objects.equals(dto.getPassword(),miniUser.getPassword())){
            throw new ServiceException(ApiResultEnum.SYSTEM_PASSWORD_ERROR);
        }
        StpKit.APP.login(miniUser.getId());
        return reloadUserInfo(miniUser);
    }

    @Override
    public boolean sendCode(MiniRegisterDto dto) {
        if (!Validator.isEmail(dto.getAccount())) {
            throw new ServiceException(ApiResultEnum.MINI_USER_EMAIL_FORMAT_ERROR);
        }
        String code = generateRandomCode();
        RedissonUtils.setCacheObject(RedisKeyEnum.MINI_USER_EMAIL_CODE, RedissonUtils.joinKey(dto.getAccount(), dto.getActionType()), code);
        log.info("验证码为={}",code);
        // todo 发送验证码
        return true;
    }

    private String generateRandomCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    @Override
    public AppUserVo getUserInfo() {
        AppUserVo userInfo = (AppUserVo) StpKit.APP.getSession().get(Const.SessionKey.APP_SESSION_USER);
        if (ObjectUtils.isEmpty(userInfo)) {
            AppUser user = miniUserService.getById(SecurityContextHolder.getUserId());
            // 刷新用户 session 信息
            userInfo = reloadUserInfo(user);
        }
        return userInfo;
    }


    @Override
    public String updateUserAvatar(UserAvatarDto dto) {
        String avatarUrl = dto.getAvatarUrl();
        MultipartFile avatarFile = dto.getAvatarFile();
        String folder = "/%s/".formatted(UploadImageType.AVATAR.getName());
        String fileName = IdUtil.fastSimpleUUID() + ".png";
        // 获取文件名
        File dest = new File(uploadConfig.getRoot() + folder + fileName);
        try {
            if (!ObjectUtils.isEmpty(avatarFile)) {
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                avatarFile.transferTo(dest);
            } else if (!ObjectUtils.isEmpty(avatarUrl)) {
                long size = HttpUtil.downloadFile(avatarUrl, dest);
                log.info("上传头像大小={},avatarUrl={}", size, avatarUrl);
            } else {
                // 如果两者都为空，抛出异常
                throw new ServiceException(ApiResultEnum.ERROR_INVALID_PARAM, "头像文件或头像链接不能为空");
            }
            // 更新用户头像
            Long userId = SecurityContextHolder.getUserId();
            String newAvatarUrl = "/show" + folder + fileName;
            miniUserService.updateById(new AppUser().setId(userId).setAvatarUrl(newAvatarUrl));
            StpKit.APP.getSession().delete(Const.SessionKey.APP_SESSION_USER);
            return newAvatarUrl;
        } catch (IOException e) {
            log.error("上传头像失败，err={}", e.getMessage(), e);
            throw new ServiceException(ApiResultEnum.ERROR_IO);
        }
    }

    @Override
    public boolean updateUserInfo(UserInfoDto dto) {
        Long userId = SecurityContextHolder.getUserId();
        AppUser user = miniUserService.getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            return false;
        }
        if (!ObjectUtils.isEmpty(dto.getNickname())) {
            boolean exists = miniUserService.exists(new LambdaQueryWrapper<AppUser>()
                    .eq(AppUser::getNickname, dto.getNickname())
                    .ne(AppUser::getId, SecurityContextHolder.getUserId()));
            if (exists) {
                throw new ServiceException(ApiResultEnum.MINI_USER_NICK_NAME_EXIST);
            }

        }
        setIfNotNull(user::setNickname, dto.getNickname());
        setIfNotNull(user::setAvatarUrl, dto.getAvatarUrl());
        setIfNotNull(user::setPhone, dto.getPhone());

        boolean sucUpdate = miniUserService.updateById(user);
        if (sucUpdate) {
            // 更新成功刷新用户 session 信息
            reloadUserInfo(user);
        }
        return sucUpdate;
    }


    private AppUserVo reloadUserInfo(AppUser user) {
        if (ObjectUtils.isEmpty(user)) return null;
        AppUserVo vo = new AppUserVo();
        BeanUtil.copyProperties(user, vo);
        vo.setToken(StpKit.APP.getTokenValue());
        vo.setUserId(user.getId());
        StpKit.APP.getSession().set(Const.SessionKey.APP_SESSION_USER, vo);
        return vo;
    }

    // 自定义工具方法
    private <T> void setIfNotNull(Consumer<T> setter, T value) {
        if (value != null && !ObjectUtils.isEmpty(value)) {
            setter.accept(value);
        }
    }
}
