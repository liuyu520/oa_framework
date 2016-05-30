package oa.web.controller.common;

import com.common.util.SystemHWUtil;
import com.io.hw.json.HWJacksonUtils;
import com.string.widget.util.ValueWidget;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by 黄威 on 5/30/16.<br >
 */
@Controller
@RequestMapping("/test")
public class ModelAndViewTestController {
    @RequestMapping(value = "/test/view")
    public String addSaveClientVersion(HttpServletRequest request, HttpServletResponse response, Model model
            , String targetView
            , String data) throws IOException {
        if (ValueWidget.isNullOrEmpty(targetView)) {
            //解决中文乱码问题
            response.setCharacterEncoding(SystemHWUtil.CHARSET_UTF);
            response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_PLAIN_UTF);
            PrintWriter out = response.getWriter();
            out.write("targetView can not be null");
            out.flush();
            return null;
        } else {
            if (!ValueWidget.isNullOrEmpty(data)) {
                Map map = (Map) HWJacksonUtils.deSerialize(data, Map.class);
                model.addAllAttributes(map);
            }

            return targetView;
        }

    }
}
