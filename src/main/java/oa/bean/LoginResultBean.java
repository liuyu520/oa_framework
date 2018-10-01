package oa.bean;

public class LoginResultBean {
    private boolean isFailed;
    private String message;
    private String errorCode;
    private Object value;

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
