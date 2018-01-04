package oa.util;

import com.common.dao.generic.GenericDao;
import com.common.util.RedisHelper;
import com.common.util.ReflectHWUtils;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import org.apache.log4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by whuanghkl on 3/30/16.<br >
 * spring MVC 框架专用方法
 */
public class SpringMVCUtil {
    protected final static Logger logger = Logger.getLogger(SpringMVCUtil.class);
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

    public static String getCid(HttpServletRequest request, HttpServletResponse response) {

        String conventionk = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equalsIgnoreCase("conventionk")) {
                    conventionk = c.getValue();
                }
            }
        }
        if (!ValueWidget.isNullOrEmpty(conventionk)) {
            addCookie4Cid(conventionk, request, response);
        }
        if (conventionk == null) {
            conventionk = getCookieFromSession(request, response, conventionk);
        }

        if (conventionk == null) {
            conventionk = UUID.randomUUID().toString();

            if (response != null) {
                addCookie4Cid(conventionk, request, response);
            }
//            System.out.println("3 conventionk:"+conventionk);
        }

        return conventionk;
    }

    public static String getCookieFromSession(HttpServletRequest request, HttpServletResponse response, String conventionk) {
        HttpSession session = request.getSession(false);//modified by huangweii
        if (session == null) {
            logger.warn("try to get conventionk from session ,BUT session is null");
            return conventionk;
        }
        conventionk = (String) session.getAttribute("conventionk");//modified by huangweii
        if (ValueWidget.isNullOrEmpty(conventionk)) {
            conventionk = session.getId();
        }
        if (response != null) {
            addCookie4Cid(conventionk, request, response);
        }
        return conventionk;
    }

    private static void addCookie4Cid(String conventionk, HttpServletRequest request, HttpServletResponse response) {
        String cookieKey = "conventionk";
        String setCookieHeader = response.getHeader("Set-Cookie");
        if (null != setCookieHeader && setCookieHeader.contains(cookieKey + "=" + conventionk)) {
            return;
        }
        Cookie c = new Cookie(cookieKey, conventionk);
//    	System.out.println("4 conventionk:"+conventionk);
        c.setPath("/");
        //先设置cookie有效期为4天
        c.setMaxAge(96 * 60 * 60);
//        c.setDomain(".chanjet.com");
        response.addCookie(c);
        HttpSession session = request.getSession(true);//modified by huangweii
        if (session != null) {
            session.setAttribute("conventionk", conventionk);
        } else {
            logger.warn("try to set conventionk into session ,BUT session is null");
        }
    }

    /***
     * 获取sessionId
     * @return
     */
    public static String getSessionId() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getSessionId();
    }

    public static String getKeyCache(HttpServletRequest request, HttpServletResponse response, String k) {
        String cid = SpringMVCUtil.getCid(request, response);

        if (cid == null) {
            return null;
        }
        return RedisHelper.getInstance().getKeyCache(cid, k);
    }

    public static void saveKeyCache(HttpServletRequest request, HttpServletResponse response, String k, String v) {
        String cid = SpringMVCUtil.getCid(request, response);
        if (null != v) {
            RedisHelper.getInstance().saveKeyCache(cid, k, v);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(k, v);
    }

    public static void clearKeyCache(HttpServletRequest request, HttpServletResponse response, String k) {
        String cid = SpringMVCUtil.getCid(request, response);
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
        int length = stackTraceElements.length;
        if (length > 4) {
            length = 4;
        }
        for (int i = 0; i < length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (isControllerAction(stackTraceElement)) {
                return true;
            }
        }
        return false;
    }
}
