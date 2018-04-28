package oa.bean.stub;

import java.io.File;

public class StubUpdateOption {
    private ReadAndWriteResult readAndWriteResult;
    private File file;
    private int index;

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
}
