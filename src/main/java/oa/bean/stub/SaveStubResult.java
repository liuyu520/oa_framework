package oa.bean.stub;

public class SaveStubResult extends ReadAndWriteResult {
    private String servletAction;
    private String tips;

    public String getServletAction() {
        return servletAction;
    }

    public void setServletAction(String servletAction) {
        this.servletAction = servletAction;
    }

    @Override
    public String getTips() {
        return tips;
    }

    @Override
    public void setTips(String tips) {
        this.tips = tips;
    }
}
