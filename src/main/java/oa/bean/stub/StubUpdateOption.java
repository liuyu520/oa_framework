package oa.bean.stub;

import java.io.File;

public class StubUpdateOption {
    private ReadAndWriteResult readAndWriteResult;
    private File file;
    private int index;
    private String servletAction;
    /***
     * 是否锁定,如果是锁定,则不允许修改index,但是可以修改内容
     */
    private boolean isLocked;

    public ReadAndWriteResult getReadAndWriteResult() {
        return readAndWriteResult;
    }

    public void setReadAndWriteResult(ReadAndWriteResult readAndWriteResult) {
        this.readAndWriteResult = readAndWriteResult;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getServletAction() {
        return servletAction;
    }

    public void setServletAction(String servletAction) {
        this.servletAction = servletAction;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
