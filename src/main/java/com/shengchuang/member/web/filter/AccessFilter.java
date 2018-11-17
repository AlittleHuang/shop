package com.shengchuang.member.web.filter;

import com.shengchuang.member.core.shiro.UserRealm;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤访问未登录无权访问的html
 */
@WebFilter(urlPatterns = "*.html")
public class AccessFilter implements Filter {

    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String servletPath = request.getServletPath();

        String[] ignor =
                {"/**/login.html", "/fronts/register.html", "/fronts/login_en.html", "/fronts/password-reset.html","/fronts/findword.html","/fronts/m/findword.html",
                        "/**/*.ico", "/**/*.ttf"};

        boolean matchIgnorPath = false;
        for (String path : ignor) {
            matchIgnorPath = pathMatcher.match(path, servletPath);
            if (matchIgnorPath)
                break;
        }
        if (!matchIgnorPath && pathMatcher.match("/admin/**", servletPath)
                && null == request.getSession().getAttribute(UserRealm.SESSION_NAME_ADMIN)) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendRedirect("/admin/login.html");
            return;
        }

        if (!matchIgnorPath && pathMatcher.match("/fronts/**", servletPath)
                && null == request.getSession().getAttribute(UserRealm.SESSION_NAME_USER)) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            response.sendRedirect("/fronts/login.html");
            return;

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
