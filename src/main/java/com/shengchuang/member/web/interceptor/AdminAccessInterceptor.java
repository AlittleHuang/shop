package com.shengchuang.member.web.interceptor;

import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.config.springmvc.AbstractInterceptor;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminAccessInterceptor extends AbstractInterceptor {

    @Override
    public List<String> gatPathPatterns() {
        return Arrays.asList("/admin/**/*");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean hasRole = SecurityUtils.getSubject().hasRole("admin");
        if (!hasRole && request.getServletPath().indexOf(".") < 0)
            throw new BusinessException("用户未登录").add("status", 401);
        return true;
    }
}
