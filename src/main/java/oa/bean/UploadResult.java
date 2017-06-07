package oa.bean;

import java.io.File;

/***
 * 上传的结果信息
 * @author huangwei
 * @since 2014年9月16日
 */
public class UploadResult {
	/***
	 * 是否上传成功
	 */
	private boolean isSuccess;
	/***
	 * 是否已经保存了上传的文件
	 */
	private boolean hasUploaded;
	/***
	 * 上传失败的原因
	 */
	private String errorMessage;
	
	private String dirName;
	private String savePath;
	private String saveUrl;
	private  File savedFile;
	private String finalFileName;
	/***
     * / upload/image/20150329170823_2122015-03-23_01-42-03.jpg<br />
     * 不包含项目名
     */
	private String relativePath;
	
	public UploadResult() {
		super();
	}
	public UploadResult(boolean isSuccess, boolean hasUploaded,
			String errorMessage) {
		super();
		this.isSuccess = isSuccess;
		this.hasUploaded = hasUploaded;
		this.errorMessage = errorMessage;
	}
	
	public UploadResult(boolean isSuccess, boolean hasUploaded) {
		super();
		this.isSuccess = isSuccess;
		this.hasUploaded = hasUploaded;
	}
	
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isHasUploaded() {
		return hasUploaded;
	}
	public void setHasUploaded(boolean hasUploaded) {
		this.hasUploaded = hasUploaded;
	}
	public String getDirName() {
		return dirName;
	}
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getSaveUrl() {
		return saveUrl;
	}
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
	public File getSavedFile() {
		return savedFile;
	}
	public void setSavedFile(File savedFile) {
		this.savedFile = savedFile;
	}
	public String getFinalFileName() {
		return finalFileName;
	}
	public void setFinalFileName(String finalFileName) {
		this.finalFileName = finalFileName;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	
}
