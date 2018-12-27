package oa.web.filter;

import com.common.dict.Const;
import com.common.dto.AllowOriginDto;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SimpleCORSFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SimpleCORSFilter.class);
    private AllowOriginDto allowOriginDto;
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getServletPath().endsWith("/cors/update/json")) {
            chain.doFilter(req, res);
            return;
        }
        //优先级 request > header
        String allOrigin = ObjectUtils.firstNonNull(request.getParameter("allowOrigin"), request.getHeader("Origin"), this.allowOriginDto.getAccessControlAllowOrigin());
        String message = "############## allOrigin :" + allOrigin;
        log.warn(message);
        System.out.println(message);

        response.setHeader("Access-Control-Allow-Origin", allOrigin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        String allCookie = request.getParameter("allowCookie");
        if ("true".equalsIgnoreCase(allCookie)) {//允许客户端带cookie
            response.setHeader(Const.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        } else {
            response.setHeader(Const.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(this.allowOriginDto.getAccessControlAllowCredentials()));
        }

        chain.doFilter(req, res);
        System.out.println(" :");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("SimpleCORSFilter init:");
        this.allowOriginDto = new AllowOriginDto();
        filterConfig.getServletContext().setAttribute(Const.ATTRIBUTE_ALLOW_ORIGIN_DTO, this.allowOriginDto);
    }

}
