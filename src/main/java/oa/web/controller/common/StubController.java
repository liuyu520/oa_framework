package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import com.string.widget.util.XSSUtil;
import oa.bean.stub.ReadAndWriteResult;
import oa.util.HWUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/***
 * 用于stub
 *
 * @author huangweii 2015年5月29日<br>
 * 路径中不能含有英文句点<br>
 * stub 与WEB-INF 是同级目录
 */
@Controller
@RequestMapping("/stub")
public class StubController {
    
    protected Logger logger = Logger.getLogger(this.getClass());

    /***
     * @param request
     * @param action
     * @param callback
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String corsJsonSimple(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable String action, String callback, String charset, Integer second/*模拟接口执行的时间*/
            , Integer responseCode
            , Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + action /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }

    /***
     * @param request
     * @param namespace
     * @param action
     * @param callback
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String corsJsonSimple(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable String namespace, @PathVariable String action,
                                 String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + namespace + Constant2.SLASH + action
                , callback, charset, second, responseCode, index, headerJson);
    }

    private String stubAction(HttpServletRequest request
            , HttpServletResponse response
            , String actionPath
            , String callback
            , String charset
            , Integer second
            , Integer responseCode
            , Integer index
            , String headerJson) {
        if (null == second) {
            String delay = request.getParameter("delay");
            if (!ValueWidget.isNullOrEmpty(delay)) {
                second = Integer.parseInt(delay);
            }
        }
        if (second != null && second != 0) {
            try {
                Thread.sleep(second * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (null != responseCode) {
            if (responseCode == 500) {
                throw new NullPointerException("stub test");
            }
                response.setStatus(responseCode);
                return null;
            }
        if (ValueWidget.isNullOrEmpty(charset)) {
            charset = SystemHWUtil.CHARSET_UTF;
        }
        actionPath = XSSUtil.deleteXSS(actionPath);
        System.out.println("访问:" + actionPath);
        ReadAndWriteResult readAndWriteResult = HWUtils.stub(request, actionPath, charset, index);

        if (!readAndWriteResult.isResult()) {
            logger.error(readAndWriteResult.getErrorMessage());
        }
        String content = readAndWriteResult.getContent();
        if (!readAndWriteResult.isResult()
                && ValueWidget.isNullOrEmpty(content)) {
            content = readAndWriteResult.getErrorMessage();
        }
        logger.info(SystemHWUtil.CRLF + content);
        addResponseHeader(response, headerJson);
        return HWJacksonUtils.getJsonP(content, callback);
    }

    private void addResponseHeader(HttpServletResponse response, String headerJson) {
        if (!ValueWidget.isNullOrEmpty(headerJson)) {
            Map<String, String> headerMap = (Map) HWJacksonUtils.deSerialize(headerJson, HashMap.class);
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                response.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }


    @ResponseBody
    @RequestMapping(value = "/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + group + Constant2.SLASH + namespace + Constant2.SLASH + action
                /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String version,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + version + Constant2.SLASH + group + Constant2.SLASH + namespace + Constant2.SLASH + action
                /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String version,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + version + Constant2.SLASH + module + Constant2.SLASH + group + Constant2.SLASH + namespace + Constant2.SLASH + action
                /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{branch}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String version,
                             @PathVariable String branch,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + version + Constant2.SLASH + branch + Constant2.SLASH + module + Constant2.SLASH + group + Constant2.SLASH + namespace + Constant2.SLASH + action
                /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{branch}/{branch2}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String version,
                             @PathVariable String branch,
                             @PathVariable String branch2,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset, Integer second/*模拟接口执行的时间*/, Integer responseCode, Integer index, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + version + Constant2.SLASH + branch + Constant2.SLASH + branch2 + Constant2.SLASH + module + Constant2.SLASH + group + Constant2.SLASH + namespace + Constant2.SLASH + action
                /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index, headerJson);
    }


}
