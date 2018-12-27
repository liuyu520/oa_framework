package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理类型:1-先抢先得,2-独家代理,3-指定经纪人;4-房主自己发布<br />
 * ;25-独家代理给门店;;<br />35-代理出售给门店
 * 前端选择"独家代理",只能选择一个经纪人;<br />
 * 选择代理,就会进入下一个界面:有两个选项:先抢先得,指定经纪人
 */
public enum EGrabAgentType {
    UNIQUE_AGENT(2, "独家代理"),
    APPOINT_AGENT(3, "房主指派"),//接口:/house/assign/agentlist/json
    RANDOM_AGENT(1, "一键派单"),
    HOUSE_OWNER(4, "房主自主发布"),
    UNIQUE_TO_Store(25, "门店独家代理"),
    APPOINT_TO_Store(35, "指派给门店");
    private int type;
    private String label;

    EGrabAgentType(int type, String label) {
        this.type = type;
        this.label = label;
    }

    private static Map<Integer, EGrabAgentType> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EGrabAgentType c : EGrabAgentType.values()) {
            grabOrderStatusMap.put(c.getType(), c);
            orderStatusLabelMap.put(c.type, c.toString());
        }
    }

    public static EGrabAgentType query(int type) {
        return grabOrderStatusMap.get(type);
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EGrabAgentType inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    /***
     * 打印枚举类所有状态含义
     * @return
     */
    public static String getAllEnumDoc() {
        return Joiner.on(SystemHWUtil.CRLF).withKeyValueSeparator(":").join(orderStatusLabelMap);
    }
}
