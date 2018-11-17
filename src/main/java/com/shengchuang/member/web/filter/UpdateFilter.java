package com.shengchuang.member.web.filter;

import com.shengchuang.member.additional.service.setting.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import java.io.IOException;

/**
 *
 */
//@WebFilter
public class UpdateFilter implements Filter {

    @Autowired
    private SettingsService settingsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
