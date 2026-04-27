package com.lrs.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UploadImageType {
    AVATAR("avatar"),     // 头像
    FILE("file"),     // 文件
    PRODUCT("product"),     // 产品
    OTHER("other"),     // 商品

    ;
    private String name;


    public static UploadImageType matchName(String name){
        if(StrUtil.isBlank(name)){
            return AVATAR;
        }
        for (UploadImageType value : UploadImageType.values()) {
            if(value.getName().equalsIgnoreCase(name)){
                return value;
            }
        }
        return OTHER;
    }
}
