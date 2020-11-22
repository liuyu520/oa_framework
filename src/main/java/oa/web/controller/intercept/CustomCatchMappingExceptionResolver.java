package oa.web.controller.intercept;

import com.common.bean.BaseResponseDto;
import com.common.util.SystemHWUtil;
import com.time.util.TimeHWUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 黄威 on 29/11/2016.<br >
 * 全局捕获异常
 */
public class CustomCatchMappingExceptionResolver extends org.springframework.web.servlet.handler.SimpleMappingExceptionResolver {
    private final static Logger log = LoggerFactory.getLogger(CustomCatchMappingExceptionResolver.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {
        Map<String, Exception> model = new HashMap<String, Exception>();
        model.put("ex", ex);
//        ModelAndView modelAndView = new ModelAndView("../../exception/errorPage",model);

        /*错误日志输出到控制台*/
        log.error(TimeHWUtil.getCurrentDateTime());
        log.error(ex.getMessage(), ex);
        if (handler instanceof HandlerMethod) {
            ResponseBody responseBody = ((HandlerMethod) handler).getMethodAnnotation(ResponseBody.class);
            if (null != responseBody) {
                PrintWriter out = null;
                response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF);
                try {
                    out = response.getWriter();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage(), e);
                }
                out.print(new BaseResponseDto("5000", ex.getMessage()).toJson());
                out.flush();
                out.close();//不能缺少,否则就会一直请求,前端报错:net::ERR_INCOMPLETE_CHUNKED_ENCODING
            }
        }
        return null;
    }
}
