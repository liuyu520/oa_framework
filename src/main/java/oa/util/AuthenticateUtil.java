package oa.util;

import com.common.dict.Constant2;
import com.common.entity.user.interf.GenericUser;
import com.string.widget.util.ValueWidget;

import javax.servlet.http.HttpSession;

/**
 * Created by huangweii on 2016/2/20.
 */
public class AuthenticateUtil {
    /***
     * 判断是否已登录
     *
     * @param user2
     * @return
     */
    public static boolean isLogined(GenericUser user2, String loginFlag) {
        if (ValueWidget.isNullOrEmpty(user2) || ValueWidget.isNullOrEmpty(user2.getUsername())
                || (!Constant2.FLAG_LOGIN_SUCCESS.equalsIgnoreCase(loginFlag))) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * 判断是否已登录
     *
     * @param session
     * @return
     */
    public static boolean isLogined(HttpSession session) {
        String loginFlag = (String) session
                .getAttribute(Constant2.SESSION_KEY_LOGINED_FLAG);
        GenericUser user2 = (GenericUser) session.getAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        return isLogined(user2, loginFlag);
    }

    /**
     * 为了记录日志时,记录用户id和用户名
     *
     * @param session
     * @return
     */
    public static GenericUser logout(HttpSession session) {
        GenericUser user = (GenericUser) session.getAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        session.removeAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        session.removeAttribute(Constant2.SESSION_KEY_LOGINED_FLAG);
        return user;//用于记录日志时记录用户名
//		session.invalidate();//TODO 千万不能执行
    }
}
