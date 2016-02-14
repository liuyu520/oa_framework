package oa.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * 
 * @author Administrator
 *	访问app的日志
 */
@Entity
@Table(name="t_access_log")
public class AccessLog {
	private int id;
	/***
	 * 设备类型android or ios
	 */
	private String osType;
	/***
	 * 设备标示（device token or clientid）
	 */
	private String deviceId;
	private long time;
	/***
	 * 访问的日期,例如”2015-02-26”
	 */
	private String accessDay;
	/***
	 * 日期和时间
	 */
	private String accessDayTime;
	/***
	 * 内网ip
	 */
	private String ip;
	/***
	 * 外网ip
	 */
	private String extranetIp;
	private String description;
	/***
	 * 1:访问页面;2:离开页面 ;3,上传文件;4,下载文件
	 */
	private int accessType;
	private String reserved;
	/***
	 * 登录用户的数据库ID
	 */
	private int userId;
	private String username;
	/***
	 * /SSLServer/addUser.security
	 */
	private String requestURI;
	/***
	 * 请求的参数,例如"username=whuang&password=root"
	 */
	private String queryString;
	/**
	 * 客户端userAgent
	 */
	private String userAgent;
	/***
	 * 请求的目标
	 */
	private String requestTarget;
	/***
	 * 操作结果
	 */
	private String operateResult;
	/***
	 * Pad或Phone
	 */
	private String deviceType;
	private String sessionId;
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="os_type")
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	
	@Column(name="device_id")
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	@Column(name="access_day")
	public String getAccessDay() {
		return accessDay;
	}
	public void setAccessDay(String accessDay) {
		this.accessDay = accessDay;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	@Column(name="access_type")
	public int getAccessType() {
		return accessType;
	}
	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}
	
	@Column(name="extranet_ip")
	public String getExtranetIp() {
		return extranetIp;
	}
	public void setExtranetIp(String extranetIp) {
		this.extranetIp = extranetIp;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRequestURI() {
		return requestURI;
	}
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	@Column(name="query_string")
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	@Column(name="user_agent")
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	@Column(name="request_target")
	public String getRequestTarget() {
		return requestTarget;
	}
	public void setRequestTarget(String requestTarget) {
		this.requestTarget = requestTarget;
	}
	@Column(name="operate_result")
	public String getOperateResult() {
		return operateResult;
	}
	public void setOperateResult(String operateResult) {
		this.operateResult = operateResult;
	}
	@Column(name="device_type")
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	@Column(name="access_daytime")
	public String getAccessDayTime() {
		return accessDayTime;
	}
	public void setAccessDayTime(String accessDayTime) {
		this.accessDayTime = accessDayTime;
	}
	@Column(name="session2_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
