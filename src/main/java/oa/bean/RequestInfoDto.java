package oa.bean;


import com.common.annotation.ColumnDescription;

import java.util.Map;

public class RequestInfoDto {
    private String contextPath;
    private String requestURL;
    private String requestURI;
    private String servletPath;
    private String requestedSessionId;
    private String contentType;
    private String formParameters;
    private String createTime;
    /***
     * 创建的日期,例如”2015-02-26”
     */
    @ColumnDescription("创建的日期,例如”2015-02-26”")
    private String createDay;
    /***
     * 请求头
     */
    private Map<String, String> headerMap;
    /***
     * 请求参数<br /> 后面加2的原因是因为和request的重名<br />
     * BeanUtils.copyProperties(request, requestInfoDto);
     */
    private Map<String, String> parameterMap2;
    /**
     * redis 的cookie id
     */
    private String redisCookieId;
    /***
     * 登录用户的id
     */
    private String userId;
    /***
     * 请求方法:get or POST
     */
    private String method;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(String formParameters) {
        this.formParameters = formParameters;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateDay() {
        return createDay;
    }

    public void setCreateDay(String createDay) {
        this.createDay = createDay;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, String> getParameterMap2() {
        return parameterMap2;
    }

    public void setParameterMap2(Map<String, String> parameterMap) {
        this.parameterMap2 = parameterMap;
    }

    public String getRedisCookieId() {
        return redisCookieId;
    }

    public void setRedisCookieId(String redisCookieId) {
        this.redisCookieId = redisCookieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
