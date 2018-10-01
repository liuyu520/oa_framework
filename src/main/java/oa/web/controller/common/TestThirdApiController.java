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
    protected static final Logger logger = Logger.getLogger(TestThirdApiController.class);

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
//        URL url = new URL(apiPath);
        HttpURLConnection httpUrlConnection = HttpSocketUtil.getHttpURLConnection(apiPath, isSsl, null);
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
     * Content-Type should not be "application/x-www-form-urlencoded;charset=UTF-8"<br />
     * request contentType:application/json;charset=UTF-8
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
//        Object[] resultArr = responseResult.getResultArr();
        int resCode = responseResult.getResCode();
        String jsonResult = responseResult.getResponseJsonResult();
        Map map = new HashMap();
        map.put("responseCode", resCode);
        map.put("responseText", jsonResult);

        return HWJacksonUtils.getJsonP(map);
    }

    @RequestMapping(value = "/innerSign", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String innerSign(@RequestParam(required = false, defaultValue = "1") String type,
                            @RequestParam(required = false, defaultValue = "2") String netType,
                            @RequestParam(required = false) String authorization,
                            @RequestParam(required = false) String Authorization,
                            @RequestParam(required = false, defaultValue = "") String host
    ) throws IOException, KeyManagementException, NoSuchAlgorithmException {
// 签到
        com.common.bean.RequestSendChain requestInfoBeanOrderBnz = new com.common.bean.RequestSendChain();
        requestInfoBeanOrderBnz.setServerIp(host);
        requestInfoBeanOrderBnz.setSsl(false);
        requestInfoBeanOrderBnz.setPort("80");
        requestInfoBeanOrderBnz.setActionPath("/app/rest/innerSign/add?orgId=90003720382");
        requestInfoBeanOrderBnz.setRequestCookie("");
        requestInfoBeanOrderBnz.setCustomRequestContentType("");
        requestInfoBeanOrderBnz.setRequestMethod(com.common.dict.Constant2.REQUEST_METHOD_POST);
        // requestInfoBeanOrderBnz.setDependentRequest(requestInfoBeanLogin);
        requestInfoBeanOrderBnz.setCurrRequestParameterName("");
        requestInfoBeanOrderBnz.setPreRequestParameterName("");
        if (ValueWidget.isNullOrEmpty(authorization)) {
            authorization = Authorization;
        }
        /*if(ValueWidget.isNullOrEmpty(authorization)){
            authorization="8124B7786EF98B6EAE3A5AC8BD6F9B7FD915B6B4E12813E069C5F1CCE23F1041B392D9B54AEFF4FAF7D5C0553D8CE4036C800C7FD33968878525024D66043DCBEC97B1497A0952BD53C1ED148F61E954724AEC4915AC2AF32D48765C5E1B55C8";
        }*/
        java.util.TreeMap parameterMapSnAN = new java.util.TreeMap();//请求参数
        parameterMapSnAN.put("{DES}/jVtMAHuT1k=", "{DES}fpNSygs55lpnVBj6rO7FzReAucFefdcfRD1JRNSpQ4SulZ/g5hvulg==");
        parameterMapSnAN.put("{DES}gfUUxw79kM0pxktdDXuaIQ==", "{DES}EfOfhHlPOVI=");
        parameterMapSnAN.put("{DES}ahg1TAT803W5c3Qxiv/pjg==", "{DES}EfOfhHlPOVI=");
        parameterMapSnAN.put("deviceId", "864587025867988");
        parameterMapSnAN.put("isLightApp", "1");
        parameterMapSnAN.put("netType", netType);
        parameterMapSnAN.put("type", type);
        parameterMapSnAN.put("wifiIdentify", "38%3A97%3Ad6%3Ad2%3A1e%3A41");
        parameterMapSnAN.put("wifiName", "aaa");
        requestInfoBeanOrderBnz.setRequestParameters(parameterMapSnAN);
        requestInfoBeanOrderBnz.updateRequestBody();


        org.apache.commons.collections.map.ListOrderedMap header = new org.apache.commons.collections.map.ListOrderedMap();
        header.put("Authorization", authorization);
        header.put("gzq-sig", "0002|58328c926023997437d684726e77b613");
        header.put("Host", host);
        requestInfoBeanOrderBnz.setHeaderMap(header);

        com.common.bean.ResponseResult responseResultOrderXbT = requestInfoBeanOrderBnz.request(); //new RequestPanel.ResponseResult(requestInfoBeanLogin).invoke();
        String responseOrderTAs = responseResultOrderXbT.getResponseJsonResult();
        System.out.println("responseText:" + responseOrderTAs);
        System.out.println(com.io.hw.json.JSONHWUtil.jsonFormatter(responseOrderTAs));
        return responseOrderTAs + SystemHWUtil.CRLF;
    }

    /**
     * 京东签到领京豆
     *
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "/getBeans", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    @ResponseBody
    public String getBeans() throws IOException, KeyManagementException, NoSuchAlgorithmException {
// 京东领京豆
        com.common.bean.RequestSendChain requestInfoBeanOrdermbw = new com.common.bean.RequestSendChain();
        requestInfoBeanOrdermbw.setServerIp("ld.m.jd.com");
        requestInfoBeanOrdermbw.setSsl(true);
        requestInfoBeanOrdermbw.setPort("80");
        requestInfoBeanOrdermbw.setActionPath("/SignAndGetBeansN/signStart");
        requestInfoBeanOrdermbw.setRequestCookie("pt_key=app_openAAFY9NydADDfImiRK13EAVAFTjk9oeM523klkYPDAtkkuBIOKcVyK2z8Tu0joOt_w4_lqHwmgvc;pt_pin=whuanghkl;pwdt_id=whuanghkl;sid=a73a5d58aa734e5b7fa783644732725w;JSESSIONID=C528B52C2631E897B219C71291909071.s1;pre_session=864587025867988-c0eefb05dec4|127;pre_seq=2;__jdv=122270672|jdzt_refer_null|t_108549027_1|jzt-zhitou|ak4nawahaaaonve6b53a|1490073600000");
        requestInfoBeanOrdermbw.setCustomRequestContentType("");
        requestInfoBeanOrdermbw.setRequestMethod(com.common.dict.Constant2.REQUEST_METHOD_GET);
        // requestInfoBeanOrdermbw.setDependentRequest(requestInfoBeanLogin);
        requestInfoBeanOrdermbw.setCurrRequestParameterName("");
        requestInfoBeanOrdermbw.setPreRequestParameterName("");

        java.util.TreeMap parameterMaphrkI = new java.util.TreeMap();//请求参数
        requestInfoBeanOrdermbw.setRequestParameters(parameterMaphrkI);
        requestInfoBeanOrdermbw.updateRequestBody();

        com.common.bean.ResponseResult responseResultOrdertnX = requestInfoBeanOrdermbw.request(); //new RequestPanel.ResponseResult(requestInfoBeanLogin).invoke();
        String responseOrderUlH = responseResultOrdertnX.getResponseJsonResult();
        System.out.println("responseText:" + responseOrderUlH);
        System.out.println(com.io.hw.json.JSONHWUtil.jsonFormatter(responseOrderUlH));
        return responseOrderUlH + SystemHWUtil.CRLF;
    }
}
