package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import com.string.widget.util.XSSUtil;
import oa.util.HWUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
//@RequestMapping("/stubIndex")

/***
 * @author whuanghkl
 *         需要子类继承
 */
public class StubIndexController {
    protected static Logger logger = Logger.getLogger(StubIndexController.class);

    private static String getNginxScript(String targetUrl, String stubUrl) {
        if (ValueWidget.isNullOrEmpty(stubUrl)) {
            stubUrl = "www.yhskyc.com/";
        }
        return ValueWidget.getNginxDispatch(targetUrl, stubUrl);
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
        logger.debug("/old_content sessionId:" + session.getId());
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

    @ResponseBody
    @RequestMapping(value = "/nginx_dispatch_json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String nginxDispatchJson(HttpServletRequest request, String targetUrl, String stubUrl) {
        stubUrl = XSSUtil.deleteXSS(stubUrl);
        targetUrl = XSSUtil.deleteXSS(targetUrl);
        String nginxDispatchCmd = getNginxScript(targetUrl, stubUrl);
        Map map = new HashMap();
        map.put("cmd", nginxDispatchCmd);
        return HWJacksonUtils.getJsonP(map);
    }

    @ResponseBody
    @RequestMapping(value = "/nginx_dispatch", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String nginxDispatch(HttpServletRequest request, String targetUrl, String stubUrl) {
        stubUrl = XSSUtil.deleteXSS(stubUrl);
        targetUrl = XSSUtil.deleteXSS(targetUrl);
        String nginxDispatchCmd = getNginxScript(targetUrl, stubUrl);
        return nginxDispatchCmd;
    }
}
