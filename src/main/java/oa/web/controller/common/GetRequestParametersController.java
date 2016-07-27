package oa.web.controller.common;

import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/***
 *
 * @author huangweii 2015年6月29日
 */
@Controller
@RequestMapping("/info")
public class GetRequestParametersController {
    public static Map getRequestMap(HttpServletRequest request) throws IOException {
        String requestBody = WebServletUtil.getRequestPostStr(request);
        String path = request.getContextPath();
        String basePath = WebServletUtil.getBasePath(request);
        System.out.println("basePath:" + basePath);
        String charEncoding = request.getCharacterEncoding();
        String queryStr = WebServletUtil.getRequestQueryStr(request, null);
        Map parameterMap = request.getParameterMap();
        String queryString = request.getQueryString();
        Map map = new HashMap();
        System.out.println(SystemHWUtil.DIVIDING_LINE);
        map.put("requestBody", requestBody);
        if (null != queryStr) {
            queryStr = queryStr.replace("\u0000", SystemHWUtil.EMPTY);
        }
        map.put("queryStr", queryStr);
        map.put("parameterMap", parameterMap);
        map.put("request charEncoding", charEncoding);
        map.put("queryString", queryString);
        if (ValueWidget.isNullOrEmpty(charEncoding)) {
            charEncoding = SystemHWUtil.CHARSET_UTF;
        }
        if (!ValueWidget.isNullOrEmpty(queryString)) {
            map.put("decode queryString", URLDecoder.decode(queryString, charEncoding));
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/request0", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameter4(HttpServletRequest request
            , String name, int age, String address) throws IOException {
        String charEncoding = request.getCharacterEncoding();
        Map map = new HashMap();
        map.put("request charEncoding", charEncoding);
        map.put("name", name);
        map.put("age", age);
        map.put("address", address);
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/request_a", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParametera(HttpServletRequest request) throws IOException {
        Map map = getRequestMap(request);
        map.put("api", "request_a");
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/request_b", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameterb(HttpServletRequest request) throws IOException {
        Map map = getRequestMap(request);
        map.put("api", "request_b");
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/request3", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameter3(HttpServletRequest request) throws IOException {
        String requestBody = WebServletUtil.getRequestPostStr(request);
        String queryStr = WebServletUtil.getRequestQueryStr(request, null);
        Map parameterMap = request.getParameterMap();
        String queryString = request.getQueryString();
        Map map = new HashMap();
        System.out.println("request3");
        map.put("requestBody", requestBody);
        map.put("queryStr", queryStr);
        map.put("parameterMap", parameterMap);
        map.put("queryString", queryString);
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/request4", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameter4(HttpServletRequest request) throws IOException {
        Map parameterMap = request.getParameterMap();
        String queryString = request.getQueryString();
        Map map = new HashMap();
        System.out.println("request4");
        map.put("parameterMap", parameterMap);
        map.put("queryString", queryString);
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/request2", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameter2(HttpServletRequest request
            /*,String client_secret
			,String auth_code
			,String client_id
			,String password
			,String auth_username
			,String appKey*/) throws IOException {
        String requestBody = WebServletUtil.getRequestPostStr(request);
        String queryStr = WebServletUtil.getRequestQueryStr(request, null);
        Map map = new HashMap();
        map.put("requestBody", requestBody);
        map.put("queryStr", queryStr);
		/*System.out.println("client_secret:"+client_secret);
		System.out.println("auth_code:"+auth_code);
		System.out.println("client_id:"+client_id);
		System.out.println("password:"+password);
		System.out.println("auth_username:"+auth_username);
		System.out.println("appKey:"+appKey);*/
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/parameterMap", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String getParameterMap(HttpServletRequest request) throws IOException {
        Map parameterMap = request.getParameterMap();
        Map map = new HashMap();
        map.put("parameterMap", parameterMap);
        return HWJacksonUtils.getJsonP(map);
    }

}
