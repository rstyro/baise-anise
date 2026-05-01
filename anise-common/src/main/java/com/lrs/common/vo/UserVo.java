package com.lrs.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

@Accessors(chain = true)
@Data
public class UserVo {

    // "用户ID"
    private Long userId;

    //"token"
    private String token;

    //"昵称"
    private String nickname;

    //"头像图片地址"
    private String avatarUrl;

    //"用户的性别，值为1时是男性，值为2时是女性，值为0时是未知"
    private Short sex;

    //"手机号码"
    private String phone;
}
