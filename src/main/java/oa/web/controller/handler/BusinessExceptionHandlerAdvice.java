package oa.web.controller.handler;

import com.common.bean.BaseResponseDto;
import com.common.bean.exception.LogicBusinessException;
import com.common.util.BusinessExceptionUtil;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.string.widget.util.ValueWidget;
import oa.util.SpringMVCUtil;
import org.apache.log4j.Logger;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by whuanghkl on 3/30/16.
 * 注意使用注解@ControllerAdvice作用域是全局Controller范围
 * 可应用到所有@RequestMapping类或方法上的@ExceptionHandler、@InitBinder、@ModelAttribute，在这里是@ExceptionHandler<br />
 * 用于检测第三方接口,比如bsvc或cia的504,502等异常<br />
 * 这些异常均属于非业务异常,与业务毫无关系,所以单独处理<br />
 * 注意:StoreBusinessException 不要捕获,否则无法被BusinessExceptionHandlerAdvice 截获<br />
 * 注意:传递url中的参数如果可能包含中文一定要URL编码,
 */
@ControllerAdvice
public class BusinessExceptionHandlerAdvice {
    public static Logger logger = Logger.getLogger(BusinessExceptionHandlerAdvice.class);


    @ExceptionHandler(LogicBusinessException.class)
//    @RESPONSE_CONTENTTYPE_JSON_UTFStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
    public String handleBusinessException(LogicBusinessException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//        return ClassUtils.getShortName(ex.getClass()) + ex.getMessage();
        logger.error(ex);//{errorCode='1021', errorMessage='用户不在组织的企业客户身份中'}
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        logger.error("old url:" + request.getRequestURL());
        logger.error("query string:" + request.getQueryString());
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        /*StackTraceElement stackTraceElement=stackTraceElements[0];

        System.out.println("00 :" + isControllerAction(stackTraceElement));
        System.out.println("11 :" + isControllerAction(stackTraceElements[1]));
        System.out.println("0 :" +stackTraceElement.getMethodName());
        System.out.println("1 :" +stackTraceElements[1].getClass());*/
        if (!ex.isWap()) {
            boolean isControllerAction = SpringMVCUtil.isControllerAction(stackTraceElements);
            System.out.println("isControllerAction :" + isControllerAction);
            if (isControllerAction) {
                ex.setWap(true);//表示返回json
            }
        }

        if (ex.isWap() || WebServletUtil.getMobileOsInfo(request).isMobile()) {//如果是手机端
            PrintWriter out = null;
            response.setContentType(SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF);
            try {
                out = response.getWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ValueWidget.isNullOrEmpty(ex.getResponseBody())) {
                out.print(new BaseResponseDto(ex.getErrorCode(), ex.getErrorMessage()).toJson());
            } else {
                out.print(ex.getResponseBody());
            }
            out.flush();
        } else {
            String redirectUrl = null;
            if (ValueWidget.isNullOrEmpty(ex.getRedirectUrl())) {
                String message = null;
                message = getMessage(ex);
                redirectUrl = "/error.html?error=" + ex.getErrorCode() + "&errorMessage=" + message;
            } else {
                redirectUrl = ex.getRedirectUrl();
            }
            logger.error("redirect url:" + redirectUrl);
            try {
                response.sendRedirect(redirectUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /***
     * 响应400错误
     * @param ex
     * @param session
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler(org.springframework.beans.TypeMismatchException.class)
    public String handle400Exception2(org.springframework.beans.TypeMismatchException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return handle400Action(ex, ex.getValue(), request, response);
    }

    @ExceptionHandler({java.lang.IllegalStateException.class})
    public String handle400Exception3(java.lang.IllegalStateException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return handle400Action(ex, null, request, response);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public String handle400Exception5(HttpMediaTypeNotSupportedException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return handle400Action(ex, null, request, response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public String handle400Exception6(MethodArgumentNotValidException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return handle400Action(ex, null, request, response);
    }

    /***
     * 增加参数错误统一处理,比如 refresh 是boolean类型,但是用户传递参数"aa"<br />
     * added at 2018-08-17   中国标准时间 下午3:17:48
     * @author huangweii
     * @param ex
     * @return
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public String handleException(final MethodArgumentTypeMismatchException ex) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        baseResponseDto.setHint(ex.getMethodParameter().getParameterName())
                .setErrorCode("1001");
        return baseResponseDto.toJson();
    }


    @ExceptionHandler({BindException.class})
    public String handle400Exception4(BindException ex, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String respCode = String.valueOf(HttpServletResponse.SC_BAD_REQUEST);
        logger.error(respCode, ex);
        LogicBusinessException logicBusinessException = new LogicBusinessException();
        logicBusinessException.setErrorCode(respCode);
        BindingResult bindingResult = ex.getBindingResult();

        String errorMessage;
        if (null == bindingResult || null == bindingResult.getFieldError()) {
            errorMessage = "请确认 API接口路径中 \"{status}\"中的占位符不是成员变量名称," + ex.getMessage();
        } else {
            errorMessage = bindingResult.getFieldError().getDefaultMessage();
        }
        System.out.println("errorMessage :" + errorMessage);
        logger.error(errorMessage);
        logicBusinessException.setErrorMessage(errorMessage);
        BusinessExceptionUtil.dealException(logicBusinessException, response, null);
        return null;
    }

    private String handle400Action(Exception ex, Object value, HttpServletRequest request, HttpServletResponse response) {
        String respCode = String.valueOf(HttpServletResponse.SC_BAD_REQUEST);
        logger.error(respCode, ex);
        LogicBusinessException logicBusinessException = new LogicBusinessException();
        logicBusinessException.setErrorCode(respCode);
        logicBusinessException.setErrorMessage((value == null ? "" : value + " ") + ex.getMessage());
        BusinessExceptionUtil.dealException(logicBusinessException, response, null);
        return null;
    }

    /***
     *
     * @param ex
     * @param message :中文已经经过url 编码
     * @return
     */
    private static String getMessage(LogicBusinessException ex) {
        String message = null;
        if (ValueWidget.isNullOrEmpty(ex.getErrorMessage())) {
            message = SystemHWUtil.EMPTY;
        } else {
            try {
                message = URLEncoder.encode(ex.getErrorMessage(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
}
