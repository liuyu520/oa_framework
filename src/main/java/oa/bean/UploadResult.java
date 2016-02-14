package oa.bean;
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
	
	
}
