package oa.web.controller.common;

import com.common.util.SystemHWUtil;
import oa.util.SpringMVCUtil;
import oa.web.controller.generic.GenericController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 黄威 on 10/11/2016.<br >
 */
@Controller
@RequestMapping("/cache/")
public class CacheController extends GenericController {


    @ResponseBody
    @RequestMapping(value = "set", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String saveGlobalCache(String key, String val) {
        SpringMVCUtil.saveGlobalObject(key, val);
        return val;
    }

    @ResponseBody
    @RequestMapping(value = "get", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String getGlobalCache(String key) {
        String val = (String) SpringMVCUtil.resumeGlobalObject(key);
        if (null == val) {
            val = "(暂无数据)";
        }
        return val;
    }

    protected void beforeAddInput(Model model, HttpServletRequest request) {

    }

    protected void errorDeal(Model model) {

    }

    public String getJspFolder() {
        return null;
    }
}
