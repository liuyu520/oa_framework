package oa.web.controller.common;

import com.common.bean.RequestInfoBean;
import com.common.bean.ResponseResult;
import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.http.util.HttpSocketUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import com.string.widget.util.XSSUtil;
import oa.util.SpringMVCUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 黄威 on 4/8/16.<br >
 */
@Controller
@RequestMapping("/testapi")
public class TestThirdApiController {
    protected static Logger logger = Logger.getLogger(TestThirdApiController.class);

    /***
     * 用于测试协作方接口是否可以访问,比如403 表示拒绝访问<br>
     * 注意:若上线,则该接口需要鉴权
     *
     * @param apiPath
     * @param requestMethod
     * @return :<br>
     * {
     * apiPath: "http://i.chanjet.com/user/userAndAppInfo",
     * apiPath url encoded: "http%3A%2F%2Fi.chanjet.com%2Fuser%2FuserAndAppInfo",
     * responseCode: 401
     * }
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    @RequestMapping(value = "/testapi", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String test(@RequestParam(required = true) String apiPath, String requestMethod, boolean isSsl) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        apiPath = XSSUtil.deleteXSS(apiPath);
        if (ValueWidget.isNullOrEmpty(apiPath)) {
            logger.error("apiPath is null");
            return null;
        }
        if (!apiPath.startsWith("http")) {//例如:apiPath的值为:i.chanjet.com%2Fuser%2FuserAndAppInfo
            if (isSsl) {
                apiPath = "https://" + apiPath;
            } else {
                //自动在前面补充http://
                apiPath = "http://" + apiPath;
            }

        }
        URL url = new URL(apiPath);
        HttpURLConnection httpUrlConnection = HttpSocketUtil.getHttpURLConnection(apiPath, isSsl);
        /*urlConnection = url.openConnection();
        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;*/
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setUseCaches(false);
        if (!ValueWidget.isNullOrEmpty(requestMethod)) {
            httpUrlConnection.setRequestMethod(requestMethod);
        }
        int responseStatusCode = SystemHWUtil.NEGATIVE_ONE;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            httpUrlConnection.connect();
            responseStatusCode = httpUrlConnection.getResponseCode();
        } catch (java.net.UnknownHostException ex) {
            ex.printStackTrace();
            map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, ex.getMessage());
        } catch (java.net.ConnectException e) {
            e.printStackTrace();
            map.put(Constant2.RESPONSE_KEY_ERROR_MESSAGE, e.getMessage() + " ,可能不支持https");
        }

        httpUrlConnection.disconnect();
        logger.info("responseStatusCode:" + responseStatusCode);
        map.put("responseCode", responseStatusCode);
        map.put("apiPath", apiPath);
        map.put("apiPath url encoded", URLEncoder.encode(apiPath, SystemHWUtil.CHARSET_UTF));
        return HWJacksonUtils.getJsonP(map);
    }

    /***
     * Content-Type should not be "application/x-www-form-urlencoded;charset=UTF-8"
     *
     * @param requestInfoBean
     * @return
     */
    @RequestMapping(value = "/ajax", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String ajax(@RequestBody(required = true) RequestInfoBean requestInfoBean, HttpServletRequest request) {
        logger.debug(requestInfoBean);
        String contentType = SpringMVCUtil.getRequestContentType();
        logger.info("Content-Type:" + contentType);
        if (null != contentType && contentType.startsWith("application/x-www-form-urlencoded")) {
            logger.error("Content-Type is wrong !!!");
            return null;
        }
        if (ValueWidget.isNullOrEmpty(requestInfoBean.getActionPath())) {
            logger.error("action path is null");
            return null;
        }
        logger.info(HWJacksonUtils.getJsonP(requestInfoBean));
        ResponseResult responseResult = new ResponseResult(requestInfoBean).invoke();
        Object[] resultArr = responseResult.getResultArr();
        int resCode = responseResult.getResCode();
        String jsonResult = responseResult.getResponseJsonResult();
        Map map = new HashMap();
        map.put("responseCode", resCode);
        map.put("responseText", jsonResult);

        return HWJacksonUtils.getJsonP(map);
    }
}
