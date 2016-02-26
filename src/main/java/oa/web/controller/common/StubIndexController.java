package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import oa.util.HWUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class StubIndexController {
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
