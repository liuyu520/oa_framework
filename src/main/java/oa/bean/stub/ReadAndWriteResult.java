package oa.bean.stub;

/**
 * Created by huangweii on 2016/2/19.<br />
 * 用于stub 测试
 */
public class ReadAndWriteResult {
    private boolean result;
    private String content;
    private String errorMessage;
    /***
     * 操作成功提示信息
     */
    private String tips;
    /***
     * json 文件的绝对路径
     */
    private String absolutePath;
    /***
     * 访问地址
     */
    private String url;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
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

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
