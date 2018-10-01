package oa.web.controller.common;

import com.common.dict.Const;
import com.common.dto.AllowOriginDto;
import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类描述: 修改 跨域参数的接口. <br />
 *
 * @author hanjun.hw
 * @since 2018/10/1
 */
@RestController
@RequestMapping(value = "/cors", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
public class AllowOriginModifyController {
    @ResponseBody
    @RequestMapping(value = "/update/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public AllowOriginDto jsonUpdate2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(required = false) String allowOrigin, String allowCookie) {
        AllowOriginDto allowOriginDto = getAllowOriginDto(request);
        if (!ValueWidget.isNullOrEmpty(allowOrigin)) {
            allowOriginDto.setAccessControlAllowOrigin(allowOrigin);
        }
        if (!ValueWidget.isNullOrEmpty(allowCookie)) {
            allowOriginDto.setAccessControlAllowCredentials(Boolean.TRUE.toString().equalsIgnoreCase(allowCookie));
        }
        return allowOriginDto;
    }

    private AllowOriginDto getAllowOriginDto(HttpServletRequest request) {
        return (AllowOriginDto) request.getServletContext().getAttribute(Const.ATTRIBUTE_ALLOW_ORIGIN_DTO);
    }

    @ResponseBody
    @RequestMapping(value = "/query/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public AllowOriginDto json2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(required = false) String demo) {
        AllowOriginDto allowOriginDto = getAllowOriginDto(request);
        return allowOriginDto;
    }
}
