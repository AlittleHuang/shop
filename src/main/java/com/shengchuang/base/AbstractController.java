package com.shengchuang.base;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.pageable.ParametersMap;
import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.common.util.SystemVariables;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.config.springmvc.SessionListener;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.BalanceLogService;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.core.service.UserTreeService;
import com.shengchuang.member.core.shiro.PasswordUtil;
import com.shengchuang.member.core.shiro.UserRealm;

@RequestMapping({"", "app"})
//@CrossOrigin(origins = {"http://192.168.1.67:8006", "http://localhost:8006"})
public abstract class AbstractController implements StreamUtil {

    private static final String SESSION_NAME_USER = UserRealm.SESSION_NAME_USER;
    private static final String SESSION_NAME_ADMIN = UserRealm.SESSION_NAME_ADMIN;
    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected BalanceLogService balanceLogService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected BalanceService balanceService;
    @Autowired
    protected CommonDao commonDao;
    @Autowired
    protected UserTreeService userTreeService;
    @Autowired
    protected UserLevelService userLevelService;
    @Autowired
    protected SettingsService settingsService;

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

        boolean app = request().getServletPath().startsWith("/app/front");
        if (app && getSessionUser() == null) {
            Assert.state(getSessionUser() != null, "unlogin");
        } else if (app) {
            getSession().setMaxInactiveInterval(60 * 30);
        }
    }

    protected boolean isApp() {
        return request().getServletPath().startsWith("/app");
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
        /*ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(attributes, "");*/
        return request.get();
    }

    /**
     * 获取response实例
     *
     * @return
     */
    protected HttpServletResponse response() {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        Assert.notNull(attributes, "");
        return response.get();
    }

    /**
     * 获取session实例
     *
     * @return
     */
    protected HttpSession getSession() {
        String jessionid = request().getParameter("jessionid");
        if (isApp() && jessionid != null) {
            HttpSession session = SessionListener.getSession(jessionid);
            if (session != null) return session;
        }
        return request().getSession();
    }

    /**
     * 获取上下文实例
     *
     * @return
     */
    protected ServletContext servletContext() {
        return request().getServletContext();
    }

    /**
     * map批量设置RequestAttribute
     *
     * @param m
     */
    public void setRequestAttributes(Map<String, ?> m) {
        if (m == null)
            return;
        for (Entry<String, ?> e : m.entrySet()) {
            if (e.getKey() == null || e.getValue() == null)
                continue;
            request().setAttribute(e.getKey(), e.getValue());
        }
    }

    /**
     * 获取 int 类型请求参数
     *
     * @param name
     * @return
     */
    public Integer intParameter(String name) {
        return tryGet(() -> Integer.valueOf(request().getParameter(name)));
    }

    /**
     * 获取 double 类型请求参数
     *
     * @param name
     * @return
     */
    public Double doubleParameter(String name) {
        return tryGet(() -> Double.valueOf(request().getParameter(name)));
    }

    /**
     * 获取日期类型请求参数
     *
     * @param name
     * @return
     */
    public Date dateParameter(String name) {
        return tryGet(() -> TimeUtil.parseDate(request().getParameter(name)));
    }

    /**
     * 获取日期类型请求参数
     *
     * @param name
     * @return
     */
    public LocalDate localDateParameter(String name) {
        return tryGet(() -> LocalDate.parse(request().getParameter(name)));
    }

    /**
     * 获取日期时间类型请求参数
     *
     * @param name
     * @return
     */
    public Date dateTimeParameter(String name) {
        return tryGet(() -> TimeUtil.parseDateTime(request().getParameter(name)));
    }

    /**
     * 没什么用
     *
     * @return
     */
    @SuppressWarnings("unused")
//	private User getLoginUser() {
//		User user = (User) SecurityUtils.getSubject().getPrincipal();
//		Optional<User> one = userService.findById(user.getId());
//		return one.orElse(null);
//	}

    /**
     * 前台登录用户
     * @return
     */
    public User getSessionUser() {
        return (User) getSession().getAttribute(SESSION_NAME_USER);
    }

    protected void setSessionUser(int id) {
        User user = userService.getOne(id);
        Assert.notNull(user, "user id [ " + id + " ] dosn't exists");
        if (user.getReferrerId() != null) {
            user.setReferrer(userService.getOne(user.getReferrerId()));
        }
        userLevelService.loadUscLevel(user);
        getSession().setAttribute(SESSION_NAME_USER, user);
    }

    protected void setSessionUser(User user) {
        setSessionUser(user.getId());
    }

    /**
     * 后台登录用户
     *
     * @return
     */
    protected User getSessionAdmin() {
        return (User) getSession().getAttribute(SESSION_NAME_ADMIN);
    }

    protected void setSessionAdmin(User user) {
        Assert.notNull(user, "set getSession user must not be null");
        getSession().setAttribute(SESSION_NAME_ADMIN, user);
    }

    protected void removeSessionUser() {
        getSession().removeAttribute(SESSION_NAME_USER);
    }

    protected void removeSessionAdmin() {
        getSession().removeAttribute(SESSION_NAME_ADMIN);
    }

    /**
     * 刷新前台登录用户
     *
     * @return
     */
    protected User refreshSessionUser() {
        User user = getSessionUser();
        Assert.notNull(user, "请先登录");
        user = userService.getOne(user.getId());
        setSessionUser(user);
        return user;
    }


    /**
     * 刷新前台登录用户
     *
     * @return
     */
    protected User refreshSessionAdmin() {
        User user = getSessionAdmin();
        Assert.notNull(user, "请先登录");
        user = userService.getOne(user.getId());
        setSessionAdmin(user);
        return user;
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
     * 根据请求路径获取freemarker模板路径
     *
     * @return
     */
    protected String resourcePath() {
        String requestURI = request().getServletPath();
        requestURI = requestURI.substring(0, requestURI.length() - 5);
        return requestURI;
    }

//    /**
//     * 根据请求路径获取freemarker模板真实路径(like:"/Volumes/COMMON/resource/oig/src/main/webapp/views/404.html")
//     *
//     * @return
//     */
//    protected String getRealPath() {
//        String servletPath = request().getServletPath();
//        ServletContext servletContext = request().getServletContext();
//        String path = FreeMarkerConfig.TEMPLATE_LOADER_PATH + servletPath;
//        return servletContext.getRealPath(path);
//    }

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

    /**
     * 安全toString，null 返回 null
     *
     * @param o
     */
    public String toString(Object o) {
        return toString(o, null);
    }


    /**
     * 安全toString，null 返回 null
     *
     * @param o
     */
    public String toString(Object o, String defaultValue) {
        return o == null ? defaultValue : o.toString();
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

    /**
     * 测试用
     *
     * @return
     */
    protected String getPrefixPath() {
        String preFixPath;
        if (SystemVariables.IS_WINDOWS_SYSTEM) {
            preFixPath = getBaseServerPath() + ":9082";
        } else {
            preFixPath = getBaseServerPathAndPort();
        }
        return preFixPath;
    }

    /**
     * 断言已激活，未激活抛异常
     */
    protected void assertHadActive() {
        User user = getSessionUser();
        Assert.state(user.getLevel() > User.LEVLE_0, "请先激活再进行此操作");
    }

    protected String getPhone() {
        String phone = toString(request().getParameter("phone"), "").trim();
        String region = toString(request().getParameter("region"), "+(86)").trim();

        if ("+(86)".equals(region)) {
            Assert.state(StringUtil.isPhoneNumber(phone), "手机号码错误");
        } else {
            Assert.state(StringUtil.isPureNumber(phone), "手机号码错误");
        }
        return region + phone;
    }

    /**
     * 校验交易密码，密码错误抛异常
     */
    protected void checkSecondPwd() {
        String password = request().getParameter("secondpwd");
        checkSecondPwd(password);
    }

    /**
     * 校验交易密码，密码错误抛异常
     */
    protected void checkAdminSecondPwd() {
        String secondpwd = request().getParameter("secondpwd");
        checkAdminSecondPwd(secondpwd);
    }

    private void checkAdminSecondPwd(String pwd) {
        Assert.state(StringUtil.notEmpty(pwd), "请输入密码");
        User user = refreshSessionAdmin();
        String secondPwd = user.getSecondpwd();
        secondPwd = secondPwd == null ? user.getPassword() : secondPwd;//没有二级密码用登录密码校验
        pwd = PasswordUtil.encodeStringPassword(pwd, user.getUsername());
        Assert.state(secondPwd.equals(pwd), "密码错误");
    }

    /**
     * 校验交易密码，密码错误抛异常
     */
    protected void checkSecondPwd(String pwd) {
        Assert.state(StringUtil.notEmpty(pwd), "请输入密码");
        User user = refreshSessionUser();
        String secondPwd = user.getSecondpwd();
        secondPwd = secondPwd == null ? user.getPassword() : secondPwd;//没有二级密码用登录密码校验
        pwd = PasswordUtil.encodeStringPassword(pwd, user.getUsername());
        Assert.state(secondPwd.equals(pwd), "密码错误");
    }


    protected <T, ID> ID randomId(AbstractService<T, ID> service, String sessionKey, Supplier<ID> randomId) {
        ID id = (ID) getSession().getAttribute(sessionKey);
        if (id == null || service.existsById(id)) {
            id = service.randomId(randomId);
            getSession().setAttribute(sessionKey, id);
        }
        return id;
    }

    private <T> T tryGet(Supplier<T> s) {
        try {
            return s.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取上一次的访问时间
     */
    public Long getLastRequestTime() {
        String name = request().getServletPath() + "_last_request";
        Object attribute = getSession().getAttribute(name);
        getSession().setAttribute(name, System.currentTimeMillis());
        return (Long) attribute;
    }

}
