package com.common.event;

import oa.entity.common.AccessLog;

/**
 * Created by whuanghkl on 17/6/22.<br />
 * 自定义事件
 */
public class AccessLoggerEvent {
    private AccessLog accessLog;

    public AccessLoggerEvent(AccessLog accessLog) {
        this.accessLog = accessLog;
    }

    public AccessLog getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(AccessLog accessLog) {
        this.accessLog = accessLog;
    }
}
