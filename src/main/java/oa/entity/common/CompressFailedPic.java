package oa.entity.common;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * 记录压缩失败的图片
 * @author huangwei
 * @since 2014年11月30日
 */
@Entity
@Table(name = "t_compress_failed_pic")
public class CompressFailedPic {
	private int id;
	/***
	 * 图片的实际路径
	 */
	private String picPath;
	/***
	 * 图片的原始大小
	 */
	private long originalSize;
	/***
	 * 压缩失败的原因
	 */
	private String cause;
	/***
	 * 压缩失败的时间
	 */
	private Timestamp failedTime;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="pic_path")
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	@Column(name="original_size")
	public long getOriginalSize() {
		return originalSize;
	}
	public void setOriginalSize(long originalSize) {
		this.originalSize = originalSize;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	@Column(name="failed_time")
	public Timestamp getFailedTime() {
		return failedTime;
	}
	public void setFailedTime(Timestamp failedTime) {
		this.failedTime = failedTime;
	}
	
	
}
