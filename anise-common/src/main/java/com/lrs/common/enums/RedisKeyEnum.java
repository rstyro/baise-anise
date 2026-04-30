package com.lrs.common.enums;

public enum RedisKeyEnum {
    //登录Token
    LOGIN_TOKEN("cloud:user:login_token:", 1000*3600 * 8, "登录Token"),
    REPEAT_SUBMIT("cloud:security:repeat_submit:", 5000, "请求重复提交"),
    USER_ACCOUNT_ERR_KEY("user:account:err:", 5000, "用户登录报错key"),


    MINI_USER_EMAIL_CODE("mini:code:", 1000*60*5, "注册验证码"),

    MINI_ORDER_PRODUCT("mini:order:product:", 1000*60*30, "缓存商品信息"),
    MINI_ORDER_PRODUCT_STOCK("mini:order:product:stock:", 1000*60*30, "缓存商品库存"),
    MINI_ORDER_PRODUCT_STOCK_QUEUE("mini:order:product:stock_queue", -1, "异步更新库存队列"),
    MINI_ORDER_PRODUCT_STOCK_DEAD_QUEUE("mini:order:product:stock_dead_queue", -1, "死信队列"),

    MINI_ORDER_PRODUCT_STOCK_LOCK("mini:order:product:stock:lock:", -1, "库存锁"),


    ;

    private final String key;
    /**
     * 过期时间，单位：毫秒
     */
    private final long expireTime;
    private final String message;

    RedisKeyEnum(String key, int expireTime, String message) {
        this.key = key;
        this.expireTime = expireTime;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getMessage() {
        return message;
    }

}
