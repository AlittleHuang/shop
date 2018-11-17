package com.shengchuang.member.web.interceptor;

import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.config.springmvc.AbstractInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class UpdateInterceptor extends AbstractInterceptor {

    private static List<String> contentTypes = Arrays.asList(".js", ".css", ".jpg", "jpeg", "pig", "ico", "gif", "png");
    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    SettingsService settingsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String servletPath = request.getServletPath();

        if (servletPath.startsWith("/admin") || servletPath.contains("admin")
                || servletPath.endsWith("update.html"))
            return true;

        if (settingsService.getSettings().isSystemUpdate()) {
            if (checkType(servletPath)) return true;
            response.sendRedirect("/update.html");
            return false;
        }
        return true;
    }

    private boolean checkType(String path) {
        for (String contentType : contentTypes) {
            if (path.endsWith(contentType))
                return true;
        }
        return false;
    }

    @Override
    public List<String> gatPathPatterns() {
        return Arrays.asList("/**/*");
    }
}
