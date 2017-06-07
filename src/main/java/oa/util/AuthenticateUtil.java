package oa.util;

import com.common.dict.Constant2;
import com.common.entity.user.interf.GenericUser;
import com.common.util.RedisHelper;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import oa.web.controller.generic.GenericController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        }
            return true;
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

    /***
     * 通过redis 缓存来判断
     * @param response
     * @return
     */
    public static boolean isLogined(HttpServletResponse response, Class clazz) {
        String conventionk = SpringMVCUtil.getCid(SpringMVCUtil.getRequest(), response);
        String loginFlag = RedisHelper.getInstance().getKeyCache(conventionk, Constant2.SESSION_KEY_LOGINED_FLAG);
        GenericUser user2 = null;
        user2 = getGenericUser(conventionk, clazz);
        if (isLogined(user2, loginFlag)) {//只有登录状态,才需要同步到session
            HttpSession session = SpringMVCUtil.getRequest().getSession(true);
            session.setAttribute(Constant2.SESSION_KEY_LOGINED_USER, user2);//登录成功的标识有两个:"user",Constant2.SESSION_KEY_LOGINED_FLAG
            session.setAttribute(Constant2.SESSION_KEY_LOGINED_FLAG, Constant2.FLAG_LOGIN_SUCCESS);//登录成功的标识有两个:"user",Constant2.SESSION_KEY_LOGINED_FLAG
        }
        return isLogined(user2, loginFlag);
    }

    public static GenericUser getGenericUser(String conventionk, Class clazz) {
        GenericUser user2 = null;
        String userJson = RedisHelper.getInstance().getKeyCache(conventionk, Constant2.SESSION_KEY_LOGINED_USER);
        user2 = (GenericUser) HWJacksonUtils.deSerialize(userJson, clazz);
        return user2;
    }

    /**
     * 为了记录日志时,记录用户id和用户名
     *
     * @param session
     * @return
     */
    public static GenericUser logout(HttpSession session, HttpServletResponse response) {
        HttpServletRequest request = SpringMVCUtil.getRequest();
        String uuid = SpringMVCUtil.getKeyCache(request, response, Constant2.REDIS_KEY_ACCESS_TOKEN);
        RedisHelper.getInstance().clearCache(uuid);

        GenericUser user = (GenericUser) session.getAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        session.removeAttribute(Constant2.SESSION_KEY_LOGINED_USER);
        session.removeAttribute(Constant2.SESSION_KEY_LOGINED_FLAG);
        SpringMVCUtil.clearKeyCache(request, response, Constant2.SESSION_KEY_LOGINED_USER);
        SpringMVCUtil.clearKeyCache(request, response, Constant2.SESSION_KEY_LOGINED_FLAG);
        SpringMVCUtil.clearKeyCache(request, response, Constant2.REDIS_KEY_ACCESS_TOKEN);
        return user;//用于记录日志时记录用户名
//		session.invalidate();//TODO 千万不能执行
    }

    public static boolean checkLogin(HttpServletResponse response, HttpSession session, Class clazz) {
        if (AuthenticateUtil.isLogined(session) || AuthenticateUtil.isLogined(response, clazz)/*通过redis 缓存来判断*/) {
            return true;
        }
        return false;
    }

    /***
     * 校验token
     * @param request
     * @param response
     * @return
     */
    public static boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        //判断token
        String token = request.getParameter(Constant2.REDIS_KEY_ACCESS_TOKEN);
        if (!ValueWidget.isNullOrEmpty(token)) {
            String userJson = RedisHelper.getInstance().getCache(token);
            if (!ValueWidget.isNullOrEmpty(userJson)) {
                //缓存到redis 中
                String conventionk = SpringMVCUtil.getCid(request, response);
                RedisHelper.getInstance().saveKeyCache(conventionk, Constant2.SESSION_KEY_LOGINED_FLAG, Constant2.FLAG_LOGIN_SUCCESS);
                RedisHelper.getInstance().saveKeyCache(conventionk, Constant2.SESSION_KEY_LOGINED_USER, userJson);
                return true;
            }
        }
        return false;
    }

}
