package oa.web.controller.intercept;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import oa.util.AuthenticateUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

public class MemberInterceptor implements HandlerInterceptor {
    private String errorMessage;

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
//		modelAndView=new ModelAndView(viewName)
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object arg2) throws Exception {
        HttpSession session = request.getSession(true);

        if (!AuthenticateUtil.isLogined(session)) {
            String path = request.getRequestURI();//"/demo_channel_terminal/news/list"
            System.out.println("您无权访问:" + path);
            System.out.println();
            String contextPath = request.getContextPath();
            response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);
            request.setCharacterEncoding("UTF-8");
            String message = null;
            if (ValueWidget.isNullOrEmpty(getErrorMessage())) {
                message = "您没有权限访问,请先登录.";
            } else {
                message = getErrorMessage();
            }
            log(request);
            response.sendRedirect(contextPath + getReturnUrl() + "?" + Constant2.RESPONSE_KEY_ERROR_MESSAGE + "=" + URLEncoder.encode(message, "UTF-8"));
            return false;
        }
        return true;
    }

    public void log(HttpServletRequest request) {
    }

    ;

    /**
     * 登录失败的回调地址<br />
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
