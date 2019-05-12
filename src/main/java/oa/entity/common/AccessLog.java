package oa.entity.common;

import com.common.util.SystemHWUtil;
import com.string.widget.util.ValueWidget;

import javax.persistence.*;
import java.io.Serializable;

/***
 * 
 * @author Administrator
 *	访问app的日志
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name="t_access_log")
public class AccessLog implements Serializable {
	private int id;
	/***
	 * 设备类型android or ios
	 */
	private String osType;
	/***
     * 设备标示（device token or clientid）<br />
     * 用于消息推送时,定位设备
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
	private int userId=SystemHWUtil.NEGATIVE_ONE;
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
	 * Pad,Phone 或PC
	 */
	private String deviceType;
	private String sessionId;
    /***
     * 请求的contentType
     */
    private String contentType;
    /**
     * redis 的cookie id
     */
    private String redisCookieId;
    /***
     * 七牛的secretKey
     */
    private String secretKey;
    /***
     * 七牛的 accessKey
     */
    private String accessKey;
    /***
     * 请求头<br />
     * 注意:并不是完整的请求头,而是截取有用的部分
     */
    private String requestHeaderStr;
	
	public AccessLog() {
		super();
	}
	
	public AccessLog(int accessType) {
		super();
		this.accessType = accessType;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

    public AccessLog setId(int id) {
        this.id = id;
        return this;
    }

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="os_type")
    public String getOsType() {
        return osType;
    }

    /***
     * "Android"<br />
     * "Ios"<br />
     * "WINDOWS PHONE"
     * @param osType
     */
    public AccessLog setOsType(String osType) {
        this.osType = osType;
        return this;
    }

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="device_id")
    public String getDeviceId() {
        return deviceId;
    }

    /***
     * 设备标示（device token or clientid）<br />
     * 用于消息推送时,定位设备
     * @param deviceId
     */
    public AccessLog setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
	public long getTime() {
		return time;
	}

    public AccessLog setTime(long time) {
        this.time = time;
        return this;
    }

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name="access_day")
    public String getAccessDay() {
        return accessDay;
    }

    public AccessLog setAccessDay(String accessDay) {
        this.accessDay = accessDay;
        return this;
    }
	public String getIp() {
		return ip;
	}

    public AccessLog setIp(String ip) {
        this.ip = ip;
        return this;
    }
	public String getDescription() {
		return description;
	}

    public AccessLog setDescription(String description) {
        this.description = description;
        return this;
    }
	public String getReserved() {
		return reserved;
	}

    public AccessLog setReserved(String reserved) {
        this.reserved = reserved;
        return this;
    }
	@Column(name="access_type")
	public int getAccessType() {
		return accessType;
	}

    public AccessLog setAccessType(int accessType) {
        this.accessType = accessType;
        return this;
    }
	
	@Column(name="extranet_ip")
	public String getExtranetIp() {
		return extranetIp;
	}

    public AccessLog setExtranetIp(String extranetIp) {
        this.extranetIp = extranetIp;
        return this;
    }
	public int getUserId() {
		return userId;
	}

    public AccessLog setUserId(int userId) {
        this.userId = userId;
        return this;
    }
	public String getUsername() {
		return username;
	}

    public AccessLog setUsername(String username) {
        this.username = username;
        return this;
    }
	public String getRequestURI() {
		return requestURI;
	}

    public AccessLog setRequestURI(String requestURI) {
        this.requestURI = requestURI;
        return this;
    }
	
	@Column(name="query_string")
	public String getQueryString() {
		return queryString;
	}

    public AccessLog setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }
	@Column(name="user_agent")
	public String getUserAgent() {
		return userAgent;
	}

    public AccessLog setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
	@Column(name="request_target")
	public String getRequestTarget() {
		return requestTarget;
	}

    public AccessLog setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
        return this;
    }
	@Column(name="operate_result")
	public String getOperateResult() {
		return operateResult;
	}

    public AccessLog setOperateResult(String operateResult) {
        if(!ValueWidget.isNullOrEmpty(operateResult)){
			operateResult=SystemHWUtil.splitAndFilterString(operateResult,240,false);
		}
		this.operateResult = operateResult;
        return this;
    }
	@Column(name="device_type")
	public String getDeviceType() {
		return deviceType;
	}

    public AccessLog setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }
	@Column(name="access_daytime")
	public String getAccessDayTime() {
		return accessDayTime;
	}

    public AccessLog setAccessDayTime(String accessDayTime) {
        this.accessDayTime = accessDayTime;
        return this;
    }
	@Column(name="session2_id")
	public String getSessionId() {
		return sessionId;
	}

    public AccessLog setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRedisCookieId() {
        return redisCookieId;
    }

    public void setRedisCookieId(String redisCookieId) {
        this.redisCookieId = redisCookieId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Column(name = "request_header")
    public String getRequestHeaderStr() {
        return requestHeaderStr;
    }

    public void setRequestHeaderStr(String requestHeaderStr) {
        this.requestHeaderStr = requestHeaderStr;
    }
}
