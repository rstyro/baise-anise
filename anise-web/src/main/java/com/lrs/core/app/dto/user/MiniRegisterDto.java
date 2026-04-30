package com.lrs.core.app.dto.user;

import lombok.Data;

@Data
public class MiniRegisterDto {

    // 验证码
    private String code;
    //账号/邮箱
    private String account;
    // 密码
    private String password;
    // register=注册，login=登录
    private String actionType;

}
