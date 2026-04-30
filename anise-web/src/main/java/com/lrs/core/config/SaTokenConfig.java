package com.lrs.core.config;

import cn.dev33.satoken.thymeleaf.dialect.SaTokenDialect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaTokenConfig {

    /**
     * Sa-Token 标签方言 (Thymeleaf版)
     */
    @Bean
    public SaTokenDialect getSaTokenDialect() {
        return new SaTokenDialect();
    }

}
