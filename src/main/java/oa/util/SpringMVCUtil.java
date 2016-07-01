package oa.util;

import com.common.dao.generic.GenericDao;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    public static Object resumeObject(String sessionKey) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        return httpSession.getAttribute(sessionKey);
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
}
