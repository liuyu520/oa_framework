package oa.web.controller.common;

import com.common.bean.exception.LogicBusinessException;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
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

    //    @ResponseBody
    @RequestMapping(value = "get", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF)
    public String getGlobalCache(String key) {
        validatekey22(key);
        String val = (String) SpringMVCUtil.resumeGlobalObject(key);
        if (null == val) {
            return "(暂无数据)";
        }
        return val;
    }

    public void validatekey22(String key) {
        if (ValueWidget.isNullOrEmpty(key)) {
            throw new LogicBusinessException("1001", "参数 key 为空", null);
        }
    }

    protected void beforeAddInput(Model model, HttpServletRequest request) {

    }

    protected void errorDeal(Model model) {

    }

    public String getJspFolder() {
        return null;
    }
}
