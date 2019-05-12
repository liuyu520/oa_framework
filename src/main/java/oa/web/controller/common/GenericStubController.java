package oa.web.controller.common;

import com.common.util.SystemHWUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import com.string.widget.util.XSSUtil;
import oa.bean.stub.ReadAndWriteResult;
import oa.util.HWUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述: TODO 请添加注释. <br />
 *
 * @author hanjun.hw
 * @since 2018/12/27
 */
public class GenericStubController {
    protected Logger logger = Logger.getLogger(this.getClass());

    protected String stubAction(HttpServletRequest request
            , HttpServletResponse response
            , String actionPath
            , String callback
            , String charset
            , Integer second
            , Integer responseCode
            , String index
            , String headerJson) {
        String servletPath = request.getServletPath();
        System.out.println("GenericStubController actionPath :" + actionPath + ",servletPath:" + servletPath);
        // 原因:此时 actionPath值为:"stub/api/test/demo/aaa2",但是实际url链接为"http://localhost:8080/stubb/api/test/demo/aaa2.json"
        // 缺少了后缀:".json",所以要手动增加.json,因为本地文件路径为"/Users/hanjun/Documents/mygit/person/stub_test/src/main/webapp/stub/api/test/demo/aaa2.json.xml"
        String[] pathArr = actionPath.split("/");
        if (pathArr.length > 1) {
            String suffix = pathArr[pathArr.length - 1];
            if (!servletPath.endsWith(suffix) && servletPath.contains(suffix)) {
                actionPath = actionPath + servletPath.replaceAll(".*(\\.[\\w]+)$", "$1");
            }
        }

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
        ReadAndWriteResult readAndWriteResult = HWUtils.stub(request, actionPath, charset, ValueWidget.isNumeric(index) ? Integer.parseInt(index) : null);

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

}
