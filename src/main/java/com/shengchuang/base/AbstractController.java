package com.shengchuang.base;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.pageable.ParametersMap;
import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.config.springmvc.SessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequestMapping({"", "app"})
public abstract class AbstractController implements StreamUtil {

    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected CommonDao commonDao;


    public static HttpSession getsession(HttpServletRequest request) {
        String jessionid = request.getParameter("jessionid");
        if (request.getServletPath().startsWith("/app") && jessionid != null) {
            HttpSession session = SessionListener.getSession(jessionid);
            if (session != null) return session;
        }
        return request.getSession();
    }

    @ModelAttribute
    private void modelAttributes(HttpServletRequest request, HttpServletResponse response) {
        AbstractController.request.set(request);
        AbstractController.response.set(response);
    }
    /**
     * 传入日期格式化
     *
     * @param binder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public String getAsText() throws IllegalArgumentException {
                return DateTimeFormatter.ofPattern("yyyy-MM-dd").format((LocalDate) getValue());
            }

            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        });
    }

    /**
     * 获取request
     *
     * @return
     */
    protected HttpServletRequest request() {
        return request.get();
    }

    /**
     * 获取response实例
     *
     * @return
     */
    protected HttpServletResponse response() {
        return response.get();
    }

    protected Integer getLoginUserId(){
        // TODO get login user id
        return 1;
    }

    /**
     * 协议+域名+端口号+工程名
     */
    protected String getFullContextPath() {
        HttpServletRequest request = request();
        String path = request.getContextPath();
        String tglink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return tglink + path + "/";
    }

    /**
     * @return like http://www.baidu.com:80
     */
    protected String getServerPath() {
        HttpServletRequest request = request();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    /**
     * 请求参数封装到 ParametersMap
     *
     * @return
     */
    protected ParametersMap getParametersMap() {
        return new ParametersMap(request());
    }

    /**
     * 请求参数封装到 PageRequestMap
     */
    protected PageRequestMap getPageRequestMap() {
        return new PageRequestMap(request());
    }


    protected boolean hasRole(String role) {
        return SecurityUtils.getSubject().hasRole(role);
    }

    protected String contextPath() {
        return request().getContextPath();
    }

    protected String getBaseServerPathAndPort() {
        HttpServletRequest request = request();
        int serverPort = request.getServerPort();
        String serverPortFix = serverPort == 80 ? "" : ":" + serverPort;
        return request.getScheme() + "://" + request.getServerName() + serverPortFix;
    }

    protected String getBaseServerPath() {
        HttpServletRequest request = request();
        return request.getScheme() + "://" + request.getServerName();
    }


}
