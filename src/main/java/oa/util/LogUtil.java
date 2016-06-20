package oa.util;

import com.common.bean.ClientOsInfo;
import com.common.dao.generic.GenericDao;
import com.common.dict.Constant2;
import com.common.util.ReflectHWUtils;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.RegexUtil;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;
import oa.entity.common.AccessLog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

public class LogUtil {
    /***
     * 保存日志到数据库<br>
     * 从session获取用户信息
     *
     * @param accessLog
     * @param request
     */
    public static void logSave(AccessLog accessLog, HttpServletRequest request, GenericDao accessLogDao1) {
        /*if (WebServletUtil.isLocalIp(request)) {
            System.out.println("本地服务不记录日志");
            return;
        }*/
        if (accessLog == null) {
            accessLog = new AccessLog();
        }
        //记录用户id和登录名,为什么不放在 logByMethod方法中,如果放在logByMethod中,登录时就没法获取用户id和登录名了
        HttpSession session = request.getSession(true);
        Object user2 = session.getAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        if (!ValueWidget.isNullOrEmpty(user2)) {
            try {
                Object username = ReflectHWUtils.getObjectValue(user2, "username");
                if (!ValueWidget.isNullOrEmpty(username)) {
                    accessLog.setUserId(ReflectHWUtils.getObjectIntValue(user2, "id"));
                    accessLog.setUsername((String) username);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        accessLog.setSessionId(session.getId());
        Date now = new Date();
        accessLog.setTime(TimeHWUtil.getCurrentTimeSecond(now));
        accessLog.setAccessDay(TimeHWUtil.formatDateShortEN(now));
        accessLog.setAccessDayTime(TimeHWUtil.formatDateTime(now));
        String ip = WebServletUtil.getIpAddr(request);
        System.out.println("client ip:" + ip);
        accessLog.setIp(ip);
        accessLog.setExtranetIp(ip);
        String requestURI = request.getRequestURI();
        accessLog.setRequestURI(requestURI);//例如"/SSLServer/addUser.security"
        if (accessLog.getAccessType() != Constant2.LOGS_ACCESS_TYPE_UPLOAD_FILE) {
            //上传文件,则不记录request的QueryString
            String queryString = request.getQueryString();
            if (ValueWidget.isNullOrEmpty(queryString)) {
                queryString = HWJacksonUtils.getJsonP(WebServletUtil.getParamMap(request.getParameterMap()));
                queryString = RegexUtil.filterExpression(queryString);
            }
            accessLog.setQueryString(queryString);//例如"username=whuang&password=root"
        }

        saveLog(accessLog, accessLogDao1);
    }

    /**
     * 子类需要覆写
     *
     * @param accessLog
     */
    public static void saveLog(AccessLog accessLog, GenericDao accessLogDao) {
        accessLogDao.save(accessLog);
//		logger.error("child class need to override");
    }

    public static AccessLog logByMethod(HttpServletRequest request, int accessType, String jspFolder) {
        AccessLog accessLog = new AccessLog(accessType);
       /* if (WebServletUtil.isLocalIp(request)) {
        	System.out.println("[logByMethod]本地服务不记录日志");
            return accessLog;
        }*/
//        accessLog.setAccessType();//1:访问页面;2:离开页面
        //获得当前的方法的名称
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
        String methodName = eles[3]/*调用logByMethod 的方法logInto上一个方法*/
                .getMethodName();
        accessLog.setRequestTarget(jspFolder + ":" + methodName);
        String osType = request.getParameter("osType");
        String deviceId = request.getParameter("deviceId");
        ClientOsInfo info = WebServletUtil.getMobileOsInfo(request);
        accessLog.setUserAgent(info.getUserAgent());
        accessLog.setDeviceType(info.getDeviceType());//Pad或Phone
        if (!ValueWidget.isNullOrEmpty(osType)) {
            accessLog.setOsType(osType);
        } else {
            accessLog.setOsType(info.getOsType());
        }
        if (!ValueWidget.isNullOrEmpty(deviceId)) {
            accessLog.setDeviceId(deviceId);
        }
        return accessLog;
    }

}
