package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import oa.bean.stub.ReadAndWriteResult;
import oa.util.HWUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

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
    public String corsJsonSimple(HttpServletRequest request,
                                 @PathVariable String action, String callback, String charset) {
        return stubAction(request, Constant2.stub_folder + action /*+ stub_file_Suffix*/, callback, charset);
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
        return stubAction(request, Constant2.stub_folder + namespace + Constant2.Slash + action
                , callback, charset);
    }

    private String stubAction(HttpServletRequest request, String actionPath, String callback, String charset) {
        if (ValueWidget.isNullOrEmpty(charset)) {
            charset = SystemHWUtil.CURR_ENCODING;
        }
        ReadAndWriteResult readAndWriteResult = HWUtils.stub(request, actionPath, charset);

        if (!readAndWriteResult.isResult()) {
            logger.error(readAndWriteResult.getErrorMessage());
        }
        String content = readAndWriteResult.getContent();
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
        return stubAction(request, Constant2.stub_folder + group + Constant2.Slash + namespace + Constant2.Slash + action
                /*+ stub_file_Suffix*/, callback, charset);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String version,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, Constant2.stub_folder + version + Constant2.Slash + group + Constant2.Slash + namespace + Constant2.Slash + action
                /*+ stub_file_Suffix*/, callback, charset);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String version,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, Constant2.stub_folder + version + Constant2.Slash + module + Constant2.Slash + group + Constant2.Slash + namespace + Constant2.Slash + action
                /*+ stub_file_Suffix*/, callback, charset);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{branch}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String version,
                             @PathVariable String branch,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, Constant2.stub_folder + version + Constant2.Slash + branch + Constant2.Slash + module + Constant2.Slash + group + Constant2.Slash + namespace + Constant2.Slash + action
                /*+ stub_file_Suffix*/, callback, charset);
    }

    @ResponseBody
    @RequestMapping(value = "/{version}/{branch}/{branch2}/{module}/{group}/{namespace}/{action}", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String stubAction(HttpServletRequest request,
                             @PathVariable String version,
                             @PathVariable String branch,
                             @PathVariable String branch2,
                             @PathVariable String module,
                             @PathVariable String group,
                             @PathVariable String namespace, @PathVariable String action,
                             String callback
            , String charset) {
        return stubAction(request, Constant2.stub_folder + version + Constant2.Slash + branch + Constant2.Slash + branch2 + Constant2.Slash + module + Constant2.Slash + group + Constant2.Slash + namespace + Constant2.Slash + action
                /*+ stub_file_Suffix*/, callback, charset);
    }

    @RequestMapping("/")
    public String list(HttpServletRequest request, Model model, String targetView, String keyWord) {
        List<String> stubPathList = getStubPathList(request, keyWord);
        model.addAttribute("stubPathList", stubPathList);
        model.addAttribute("keyWord", keyWord);
        if (!ValueWidget.isNullOrEmpty(targetView)) {
            return targetView;
        }
        return "list";
    }

    @ResponseBody
    @RequestMapping(value = "/listJson", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String listJson(HttpServletRequest request, Model model, String keyWord) {
        List<String> stubPathList = getStubPathList(request, keyWord);
        return HWJacksonUtils.getJsonP(stubPathList);
    }

    /***
     * stub接口 修改之前的内容
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/old_content", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String oldContent(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String content = (String) session.getAttribute(HWUtils.SESSION_KEY_STUB_OLD_CONTENT);
        if (null == content) {
            content = "(暂无)";
        }
        return content;
    }

    private List<String> getStubPathList(HttpServletRequest request) {
        return getStubPathList(request, null);
    }

    private List<String> getStubPathList(HttpServletRequest request, String keyWord) {
        String realPath2 = WebServletUtil.getRealPath(request, Constant2.stub_folder);
        return HWUtils.listStubServletPath(realPath2, keyWord);
    }
}
