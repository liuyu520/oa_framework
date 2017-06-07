package oa.web.controller.intercept;

import com.common.dict.Constant2;
import com.common.util.LoginUtil;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import oa.entity.common.AccessLog;
import oa.util.AuthenticateUtil;
import oa.util.LogUtil;
import oa.util.SpringMVCUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

public class MemberInterceptor<T> implements HandlerInterceptor {
    private String errorMessage;

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object arg2) throws Exception {
        HttpSession session = request.getSession(true);

        if (AuthenticateUtil.checkLogin(response, session, SystemHWUtil.getGenricClassType(getClass()))) return true;
        if (AuthenticateUtil.checkToken(request, response)) return true;

            String path = request.getRequestURI();//"/demo_channel_terminal/news/list"
            System.out.println("您无权访问:" + path);
        response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);
        if (requestJson(response, path)) return false;
        if (!ValueWidget.isNullOrEmpty(request.getQueryString())) {
            path = path + "?" + request.getQueryString();
        }
            //用于登录成功之后回调
            session.setAttribute(LoginUtil.SESSION_KEY_LOGIN_RETURN_URL, path);
            System.out.println();
            String contextPath = request.getContextPath();
            request.setCharacterEncoding("UTF-8");
            String message = null;
            if (ValueWidget.isNullOrEmpty(getErrorMessage())) {
                message = "您没有权限访问,请先登录.";
            } else {
                message = getErrorMessage();
            }
            log(request);

        redirectLogin(response, contextPath, message);
        return false;
    }


    public void redirectLogin(HttpServletResponse response, String contextPath, String message) throws IOException {
        String cacheContextPath = (String) SpringMVCUtil.resumeGlobalObject("convention_context");
        if (!ValueWidget.isNullOrEmpty(cacheContextPath)) {
            contextPath = cacheContextPath;
        }
        response.sendRedirect(contextPath + getReturnUrl() + "?" + Constant2.RESPONSE_KEY_ERROR_MESSAGE + "=" + URLEncoder.encode(message, "UTF-8"));
    }

    public boolean requestJson(HttpServletResponse response, String path) throws IOException {
        if (path.endsWith("/json")) {
            response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF);
            PrintWriter out = response.getWriter();
            out.print(Constant2.RESPONSE_WRONG_RESULT);
            out.flush();
            return true;
        }
        return false;
    }

    public void log(HttpServletRequest request) {
        String path = request.getRequestURI();//"/demo_channel_terminal/news/list"

        AccessLog accessLog = LogUtil.logByMethod(request, Constant2.LOGS_ACCESS_TYPE_INTO, null);
        accessLog.setDescription("access " + path);
        accessLog.setOperateResult("401");
        LogUtil.logSave(accessLog, request, SpringMVCUtil.getDao(getDaoBeanName()), true/*realSave*/);
    }

    /***
     * 子类可以覆写
     *
     * @return
     */
    public String getDaoBeanName() {
        return "accessLogDao";
    }

    /**
     * 未登录的回调地址<br />
     * 子类可以覆写
     *
     * @return
     */
    public String getReturnUrl() {
        return "/user/loginInput";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
