package oa.bean.stub;

/**
 * Created by huangweii on 2016/2/19.<br />
 * 用于stub 测试
 */
public class ReadAndWriteResult {
    private boolean success;
    private String content;
    private String errorMessage;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
