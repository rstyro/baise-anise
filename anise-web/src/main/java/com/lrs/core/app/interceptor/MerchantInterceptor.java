package com.lrs.core.app.interceptor;

import com.lrs.common.constant.Const;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.service.IBizMerchantUserService;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.util.StpKit;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 商家后台拦截器
 * 
 * <p>在商家后台请求进入时，根据当前登录用户自动获取关联的商家ID，
 * 设置到 MerchantContextHolder 中，实现数据隔离。
 * </p>
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Component
public class MerchantInterceptor implements HandlerInterceptor {

    @Resource
    private IBizMerchantUserService bizMerchantUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            UserVo user = (UserVo) StpKit.APP.getSession().get(Const.SessionKey.SESSION_USER);
            if (user != null && user.getUserId() != null) {
                Long merchantId = bizMerchantUserService.getFirstMerchantId(user.getUserId());
                if (merchantId != null) {
                    MerchantContextHolder.setMerchantId(merchantId);
                }
            }
        } catch (Exception e) {
            // 用户未登录或不是商家管理员，不设置商家上下文
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MerchantContextHolder.clear();
    }
}