package oa.web.controller.common;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.file.util.FileUtils;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;

/***
 * 用于stub
 *
 * @author huangweii 2015年5月29日
 */
@Controller
@RequestMapping("/stub")
public class StubController {
    public static final String stub_folder = "stub/";
    public static final String stub_file_Suffix = ".json";
    
    protected Logger logger = Logger.getLogger(this.getClass());

    /***
     * @param request
     * @param action
     * @param callback
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String corsJsonSimple(HttpServletRequest request,
                                 @PathVariable String action, String callback, String charset) {
        return stubAction(request, stub_folder + action + stub_file_Suffix, callback, charset);
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
    public String corsJsonSimple(HttpServletRequest request,
                                 @PathVariable String namespace, @PathVariable String action,
                                 String callback
            , String charset) {
        return stubAction(request, stub_folder + namespace + Constant2.Slash + action
                + stub_file_Suffix, callback, charset);
    }

    private String stubAction(HttpServletRequest request, String actionPath, String callback, String charset) {
        if (ValueWidget.isNullOrEmpty(charset)) {
            charset = SystemHWUtil.CURR_ENCODING;
        }
        String content = stub(request, actionPath, charset);
        logger.info(SystemHWUtil.CRLF + content);
        return HWJacksonUtils.getJsonP(content, callback);
    }


    @ResponseBody
    @RequestMapping(value = "/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, stub_folder + group + Constant2.Slash + namespace + Constant2.Slash + action
                + stub_file_Suffix, callback, charset);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String version,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, stub_folder + version + Constant2.Slash + group + Constant2.Slash + namespace + Constant2.Slash + action
                + stub_file_Suffix, callback, charset);
    }

    private String stub(HttpServletRequest request, String path) {
        return stub(request, path, SystemHWUtil.CURR_ENCODING);
    }

    /***
     * 读取文件
     *
     * @param request
     * @param path
     * @param charset
     * @return
     */
    private String stub(HttpServletRequest request, String path, String charset) {
        String content = null;
        try {
            String realPath2 = WebServletUtil.getRealPath(request, path);
            java.io.InputStream input = new FileInputStream(realPath2);
            content = FileUtils.getFullContent2(input, charset, true);
        } catch (java.io.FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
