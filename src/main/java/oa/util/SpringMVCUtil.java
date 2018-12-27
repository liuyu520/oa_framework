package oa.util;

import com.common.bean.cookie.CookieOldInfo;
import com.common.dao.annotation.SoftDelete;
import com.common.dao.generic.GenericDao;
import com.common.dict.Constant2;
import com.common.util.RedisHelper;
import com.common.util.ReflectHWUtils;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.file.hw.props.GenericReadPropsUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import oa.bean.SpringMVCContext;
import oa.web.controller.generic.GenericController;
import org.apache.log4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by whuanghkl on 3/30/16.<br >
 * spring MVC 框架专用方法
 */
public class SpringMVCUtil {
    protected final static Logger logger = Logger.getLogger(SpringMVCUtil.class);
    public static final String cookieKey = "conventionk";
    private static ThreadLocal<GenericController> currentControllerThreadLocal = new ThreadLocal<>();
    /***
     * 解决java.lang.NullPointerException
     at oa.util.SpringMVCUtil.getBean(SpringMVCUtil.java:52)
     at oa.util.SpringMVCUtil.getDao(SpringMVCUtil.java:58)
     at com.house.ujiayigou.service.OrderStatusChangeFlowService.cleanDirtyData(OrderStatusChangeFlowService.java:53)
     */
    public static WebApplicationContext applicationContext;
    private static String cookieDomain = null;
    /***
     * 根据bean name获取对象
     *
     * @param dao
     * @return
     */
    public static GenericDao getDao(String dao) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        GenericDao accessLogDao = (GenericDao) ctx.getBean(dao);
        return accessLogDao;
    }
    public static void saveObject(String sessionKey, Object object) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute(sessionKey, object);
    }

    public static ServletContext getApplication() {
        ServletContext application = null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        application = request.getSession().getServletContext();
        return application;
    }

    /***
     * 全局的变量,所有的请求都可以共享<br >
     * @param sessionKey
     * @param object
     */
    public static void saveGlobalObject(String sessionKey, Object object) {
        ServletContext application = getApplication();
        application.setAttribute(sessionKey, object);
    }

    /***
     * 全局的变量,所有的请求都可以共享<br >
     * @param sessionKey
     * @return
     */
    public static Object resumeGlobalObject(String sessionKey) {
        ServletContext application = getApplication();
        return application.getAttribute(sessionKey);
    }
    public static Object resumeObject(String sessionKey) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        return httpSession.getAttribute(sessionKey);
    }

    public static void removeObject(String sessionKey) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpSession httpSession = request.getSession();
        if (null != httpSession) {
            httpSession.removeAttribute(sessionKey);
        }
    }
    
    /***
     * 获取请求的Content-Type
     *
     * @return
     */
    public static String getRequestContentType() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return WebServletUtil.getRequestContentType(request);
    }

    /***
     * 把假数据设置到spring MVC 的model中<br >
     *
     * @param model
     * @param json
     * @return
     */
    public static Model stubModel(Model model, String json) {
        Map map = (Map) HWJacksonUtils.deSerialize(json, Map.class);
        model.addAllAttributes(map);
        return model;
    }

    public static boolean redirectLogin(HttpServletResponse response, String callbackURL) throws IOException {
        response.setStatus(401);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        response.sendRedirect(WebServletUtil.getBasePath(request) + "user/loginInput?callback=" + callbackURL);
        return false;
    }

    public static String getCallbackUrl(HttpServletRequest request) {
        String callbackUrl = request.getRequestURL().toString();
        if (!ValueWidget.isNullOrEmpty(request.getQueryString())) {
            callbackUrl = callbackUrl + "?" + request.getQueryString();
        }
        return callbackUrl;
    }

    /***
     * 在spring MVC配置文件中初始化的PropertySources,不会自动注入到ConfigurableEnvironment中,<br>
     *     所以才需要手动把PropertySources 添加进env中
     * @param propertySources : 是在spring MVC配置文件中初始化的<br>
     * @param env
     */
    public static void addCustomPropertySources(PropertySources propertySources, ConfigurableEnvironment env) {
        if (null == propertySources) {
            return;
        }
        for (Iterator<PropertySource<?>> it = propertySources.iterator(); it.hasNext(); ) {
            env.getPropertySources().addFirst((PropertySource) it.next());
        }
    }

    /***
     * 获取http请求上下文
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if (null == request) {
            return null;
        }
        return request.getParameter(name);
    }

    public static Integer getParameterInt(String name) {
        String val = SpringMVCUtil.getParameter(name);
        if (ValueWidget.isNullOrEmpty(val)) {
            return SystemHWUtil.NEGATIVE_ONE;
        }
        return Integer.parseInt(val);
    }

    /***
     * 获取spring MVC请求上下文
     * @return
     */
    public static SpringMVCContext getSpringMVCContext() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        SpringMVCContext springMVCContext = new SpringMVCContext();
        WebApplicationContext webApp = RequestContextUtils.getWebApplicationContext(request, request.getSession().getServletContext());
        springMVCContext.setRequest(request);
        springMVCContext.setResponse(response);
        springMVCContext.setWebApp(webApp);
        return springMVCContext;
    }
    /***
     *
     * @param request
     * @param beanName : 例如 userDao ,adminDao
     * @return
     */
    public static Object getBean(HttpServletRequest request, String beanName) {
        WebApplicationContext webApp = RequestContextUtils.getWebApplicationContext(request, request.getSession().getServletContext());
        return webApp.getBean(beanName);
    }

    public static CookieOldInfo getCid(HttpServletRequest request, HttpServletResponse response) {
        return getCid(request, response, false);
    }

    /***
     *  以请求参数cookie_conventionk 为准
     * @param request
     * @param response
     * @param createNew
     * @return
     */
    public static CookieOldInfo getCid(HttpServletRequest request, HttpServletResponse response, boolean createNew) {
        CookieOldInfo cookieOldInfo = null;
        String conventionk = null;
        if (!createNew) {
            cookieOldInfo = getExistConventionk(request, cookieKey);
            conventionk = cookieOldInfo.getConventionk();
            String conventionkReq = request.getParameter("cookie_" + cookieKey);
            if (!ValueWidget.isNullOrEmpty(conventionkReq)) {
                //added at 2018-07-31   中国标准时间 下午10:42:27
                conventionk = conventionkReq;
            }
            if (!ValueWidget.isNullOrEmpty(conventionk)) {
                /*if (!ValueWidget.isNullOrEmpty(conventionkReq) && (!conventionkReq.equals(conventionk))) {
                    LogicExc.throwEx("1001", "请求参数cookie_conventionk 必须与header cookie一致");
                }*/
                addCookie4Cid(conventionk, request, response);
            }

            if (conventionk == null) {
                conventionk = request.getHeader(cookieKey);//增加容错,如果cookie里面没有,则从请求头里面获取
                if (conventionk == null) {
                    conventionk = conventionkReq;
                }
                if (!ValueWidget.isNullOrEmpty(conventionk)) {
                    addCookie4Cid(conventionk, request, response);
                }

            }

            if (ValueWidget.isNullOrEmpty(conventionk)) {
                conventionk = getCookieFromSession(request, response);
            }
        }
        if (ValueWidget.isNullOrEmpty(conventionk)) {
            conventionk = "k" + UUID.randomUUID().toString() + String.valueOf(System.currentTimeMillis()).substring(7);

            if (response != null) {
                addCookie4Cid(conventionk, request, response);
            }
//            System.out.println("3 conventionk:"+conventionk);
        }
        if (null == cookieOldInfo) {
            cookieOldInfo = CookieOldInfo.getinstance(conventionk);
        } else {
            cookieOldInfo.setConventionk(conventionk);
        }
        return cookieOldInfo;
    }

    public static CookieOldInfo getExistConventionk(HttpServletRequest request) {
        return getExistConventionk(request, cookieKey);
    }

    public static CookieOldInfo getExistConventionk(HttpServletRequest request, String cookieKey) {
        String conventionk = null;
        CookieOldInfo cookieOldInfo = new CookieOldInfo();
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equalsIgnoreCase(cookieKey)) {
                    conventionk = c.getValue();
                    cookieOldInfo.setC(c);
                    cookieOldInfo.setConventionk(conventionk);
                }
            }
        }
        return cookieOldInfo;
    }

    public static String getCookieFromSession(HttpServletRequest request, HttpServletResponse response) {
        String conventionk = null;
        HttpSession session = request.getSession(false);//modified by huangweii
        if (session == null) {
            logger.warn("try to get conventionk from session ,BUT session is null");
            return null;
        }
        conventionk = (String) session.getAttribute(cookieKey);//modified by huangweii
        /*if (ValueWidget.isNullOrEmpty(conventionk)) {
            conventionk = session.getId();
        }*/
        if (conventionk != null && response != null) {
            addCookie4Cid(conventionk, request, response);
        }
        return conventionk;
    }

    private static void addCookie4Cid(String conventionk, HttpServletRequest request, HttpServletResponse response) {
        if (null == response) {
            return;
        }

        String setCookieHeader = response.getHeader("Set-Cookie");
        if (null != setCookieHeader && setCookieHeader.contains(cookieKey + "=" + conventionk)) {
            return;
        }

//        String domain=request.getRemoteHost();
        String domain = null;
        boolean hasContainsLocalhost = false;
        try {
            Properties properties = GenericReadPropsUtil.getProperties(true, "config/domain.properties");
            if (null != properties) {
                domain = properties.getProperty(Constant2.PROPERTY_KEY_COOKIE);
                cookieDomain = domain;
                String hasContainsLocalhostStr = properties.getProperty("containlocalhost");
                hasContainsLocalhost = SystemHWUtil.parse33(hasContainsLocalhostStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("domain :" + domain);//TODO 需要删除
//        System.out.println("domain :" + domain.replaceAll("^[\\w]+",""));//TODO 需要删除
        responseAddCookie(conventionk, response, cookieKey, domain);
        if (hasContainsLocalhost) {
            responseAddCookie(conventionk, response, cookieKey, "localhost");
            responseAddCookie(conventionk, response, cookieKey, "127.0.0.1");
        }

        HttpSession session = request.getSession(true);//modified by huangweii
        if (session != null) {
            session.setAttribute(cookieKey, conventionk);
        } else {
            logger.warn("try to set conventionk into session ,BUT session is null");
        }
    }

    public static void responseAddCookie(String conventionk, HttpServletResponse response, String cookieKey, String domain) {
        Cookie c = new Cookie(cookieKey, conventionk);
//    	System.out.println("4 conventionk:"+conventionk);
        c.setPath("/");//TODO ,后面抽取成配置
        //先设置cookie有效期为16天
        c.setMaxAge(96 * 4 * 60 * 60);
        if (!ValueWidget.isNullOrEmpty(domain)) {
            c.setDomain(domain);
        }
//        c.setDomain(".chanjet.com");
        response.addCookie(c);
    }

    /***
     * 获取sessionId
     * @return
     */
    public static String getSessionId() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getSessionId();
    }

    public static String getKeyCache(HttpServletRequest request, HttpServletResponse response, String k) {
        String cid = SpringMVCUtil.getCid(request, response).getConventionk();

        if (cid == null) {
            return null;
        }
        return RedisHelper.getInstance().getKeyCache(cid, k);
    }

    public static void saveKeyCache(HttpServletRequest request, HttpServletResponse response, String k, String v) {
        String cid = SpringMVCUtil.getCid(request, response).getConventionk();
        if (null != v) {
            RedisHelper.getInstance().saveKeyCache(cid, k, v);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(k, v);
    }

    public static void clearKeyCache(HttpServletRequest request, HttpServletResponse response, String k) {
        //hack 如果请求参数中传递了,则作为cid
        String hackId = "cookie_conventionk";
        String hackCid = request.getParameter(hackId);
        if (ValueWidget.isNullOrEmpty(hackCid)) {
            hackCid = (String) request.getAttribute(hackId);
        }
        String cid = null;
        //hack 的前提条件是请求参数中传递了"cookie_conventionk",并且请求头中包含"conventionk="
        if (!ValueWidget.isNullOrEmpty(hackCid) && request.getHeader("Cookie").contains("conventionk=")) {
            cid = hackCid;
        } else {
            cid = SpringMVCUtil.getCid(request, response).getConventionk();

        }
        //------- TODO 以上代码属于hack,不属于正常代码 ----------
        clearKeyCache(k, cid);
    }

    public static void clearKeyCache(String k, String cid) {
        RedisHelper.getInstance().clearKeyCache(cid, k);
    }

    /***
     * 判断接口的注解是否是ResponseBody,是,那么返回json,而不是跳转错误页面
     * @param stackTraceElement
     * @return
     */
    public static boolean isControllerAction(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        if (className.endsWith("Controller")) {
            try {
                Class controllerClass = Class.forName(className);
                Method actionMethod = ReflectHWUtils.getMethod(controllerClass, stackTraceElement.getMethodName(), ResponseBody.class);
                if (null == actionMethod) {
                    return false;
                }
                return true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 判断接口的注解是否是ResponseBody,是,那么返回json,而不是跳转错误页面<br />
     * 限制最多循环4次,否则影响性能
     * @param stackTraceElements
     * @return
     */
    public static boolean isControllerAction(StackTraceElement[] stackTraceElements) {
        int limit = 20;
        int length = stackTraceElements.length;
        if (length > limit) {
            length = limit;
        }
        for (int i = 0; i < length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (isControllerAction(stackTraceElement)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseJson(Object handler) {
        if (handler instanceof HandlerMethod) {
            ResponseBody responseBody = ((HandlerMethod) handler).getMethodAnnotation(ResponseBody.class);
            if (null != responseBody) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSoftDel(SoftDelete softDeleteAnnotation) {
        //1. 获取实体类上的注解
        //2. 判断列名是否为空
        if (null != softDeleteAnnotation && !ValueWidget.isNullOrEmpty(softDeleteAnnotation.value())) {
            return true;
        }
        return false;
    }


    public static String getServletPath() {
        HttpServletRequest request = SpringMVCUtil.getRequest();
        String servletPath = null;
        if (null != request) {
            servletPath = request.getServletPath();
//            logger.error("getServletPath:" + servletPath);
        }
        if (ValueWidget.isNullOrEmpty(servletPath)) {
            servletPath = "noRequest";
        }
        return servletPath;
    }

    public static String getCookieDomain() {
        return cookieDomain;
    }

    public static boolean modifyServPathAndSupplyUrlMap(MultiValueMap<String, RequestMappingInfo> urlMap, String lookupPath, String placeHolder) {
        if (null == lookupPath) {
            logger.error("lookupPath is null,placeHolder:" + placeHolder);
            return false;
        }
        String newLookupPath = ValueWidget.buildNewLookupPath(lookupPath, placeHolder);
        return supplyUrlMap2(urlMap, lookupPath, newLookupPath);
    }

    public static boolean supplyUrlMap2(MultiValueMap<String, RequestMappingInfo> urlMap, String lookupPath, String newLookupPath) {
        List<RequestMappingInfo> requestMappingInfos = urlMap.get(newLookupPath);
        if (!ValueWidget.isNullOrEmpty(requestMappingInfos)) {
            urlMap.put(lookupPath, requestMappingInfos);
            return true;
        }
        return false;
    }

    public static String getRequestQueryStr() throws IOException {
        return WebServletUtil.getRequestQueryStr(SpringMVCUtil.getRequest(), null);
    }


}
