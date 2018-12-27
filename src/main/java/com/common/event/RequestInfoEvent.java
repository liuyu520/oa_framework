package com.common.event;

import com.common.bean.RequestInfoDto;

import java.util.List;

/**
 * Created by whuanghkl on 17/6/22.<br />
 * 自定义事件
 */
public class RequestInfoEvent {
    private List<RequestInfoDto> requestInfoDtoList;

    public RequestInfoEvent(List<RequestInfoDto> requestInfoDtoList) {
        this.requestInfoDtoList = requestInfoDtoList;
    }

    public List<RequestInfoDto> getRequestInfoDtoList() {
        return requestInfoDtoList;
    }

    public void setRequestInfoDtoList(List<RequestInfoDto> requestInfoDtoList) {
        this.requestInfoDtoList = requestInfoDtoList;
    }
}
