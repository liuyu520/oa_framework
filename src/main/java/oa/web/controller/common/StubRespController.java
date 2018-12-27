package oa.web.controller.common;

import com.common.dict.Constant2;
import com.common.util.SystemHWUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类描述: TODO 请添加注释. <br />
 *
 * @author hanjun.hw
 * @since 2018/12/27
 */
@Controller
@RequestMapping("/stubResp")
public class StubRespController extends GenericStubController {
    /***
     * @param request
     * @param servletPath
     * @param callback
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/search", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String corsJsonSimple(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam String servletPath, String callback, String charset, Integer second/*模拟接口执行的时间*/
            , Integer responseCode
            , String index23, String headerJson) {
        return stubAction(request, response, Constant2.STUB_FOLDER + servletPath /*+ stub_file_Suffix*/, callback, charset, second, responseCode, index23, headerJson);
    }
}
