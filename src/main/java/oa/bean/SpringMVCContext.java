package oa.bean;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SpringMVCContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private WebApplicationContext webApp;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public WebApplicationContext getWebApp() {
        return webApp;
    }

    public void setWebApp(WebApplicationContext webApp) {
        this.webApp = webApp;
    }
}
