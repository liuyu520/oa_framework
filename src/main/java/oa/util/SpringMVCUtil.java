package oa.util;

import com.common.dao.generic.GenericDao;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by whuanghkl on 3/30/16.<br >
 * spring MVC 框架专用方法
 */
public class SpringMVCUtil {
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

}
