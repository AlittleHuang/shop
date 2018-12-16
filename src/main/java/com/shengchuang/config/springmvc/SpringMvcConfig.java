package com.shengchuang.config.springmvc;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.config.shiro.UserRealm;
import com.shengchuang.shop.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 配置SpringMVC
 */
@Lazy
@Configuration
public class SpringMvcConfig {

    public static final String HTML_EXTENSION = ".html";
    public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    protected final Log logger = LogFactory.getLog(SpringMvcConfig.class);

//	@Bean
//	public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
//		return new LifecycleBeanPostProcessor();
//	}
//
//	@Bean
//	public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
//		getLifecycleBeanPostProcessor();
//		DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
//		proxyCreator.setProxyTargetClass(true);
//		return proxyCreator;
//	}

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * controller异常处理
     *
     * @return ModelAndView
     */
    @Bean
    public HandlerExceptionResolver getExceptionResolver() {
        return (request, response, handler, ex) -> {
            JsonMap json;
            boolean isHtml = request.getRequestURI().endsWith(HTML_EXTENSION);
            if (ex instanceof BusinessException) {
                json = ((BusinessException) ex).getErrorMessage();
            } else {
                User user = (User) AbstractController.getsession(request).getAttribute(UserRealm.SESSION_NAME_USER);
                logger.error("user:" + user + "ip:" + getIpAddress(request) +
                        "servlet path:" + request.getServletPath(), ex);
                json = new JsonMap().failedMsg("服务器错误")
                        .add("error", ex.getLocalizedMessage());
                response.setStatus(500);
            }
            if (isHtml)
                return new ModelAndView("err").addAllObjects(json);
            return new ModelAndView(json);
        };
    }

    @Autowired
    @Bean
    public WebMvcConfigurer getWebMvcConfigurer(ApplicationContext applicationContext) {
        Collection<AbstractInterceptor> values = applicationContext.getBeansOfType(AbstractInterceptor.class).values();
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**/*").allowedOrigins("*");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                WebMvcConfigurer.super.addInterceptors(registry);
                ArrayList<AbstractInterceptor> interceptors = new ArrayList<>(values);
                interceptors.sort(AbstractInterceptor::compareTo);
                for (AbstractInterceptor interceptor : interceptors) {
                    registry.addInterceptor(interceptor).addPathPatterns(interceptor.gatPathPatterns());
                }
            }
        };
    }

//    @Bean
//    public ServletRegistrationBean<ProxyServlet> registerImageProxy() {
//        ServletRegistrationBean<ProxyServlet> bean =
//                new ServletRegistrationBean<>(new ProxyServlet(), "/image-service/*");
//
//        Map<String, String> params = new HashMap<>();
//        params.put("targetUri", "http://localhost:8033");
//        params.put("log", "true");
//
//        bean.setInitParameters(params);
//
//        return bean;
//    }

}
