package oa.util;

import com.common.dao.generic.GenericDao;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

public class SpringFrameUtil {
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
}
