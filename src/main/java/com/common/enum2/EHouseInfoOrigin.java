package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * 房源的来源<br />
 * update t_houseInfo set origin='a_free' where origin='agent'
 */
public enum EHouseInfoOrigin {
    /*AgentCreate("a_independent", "经纪人发布二手房(不需要指派)"), 通过houseType区分 */ StoreAgent("a_store", "门店经纪人房源"), FreeAgent("a_free", "自由经纪人房源)"), customer("customer", "来自买卖端");
    private String status;
    private String label;
    private static Map<String, EHouseInfoOrigin> grabOrderStatusMap = new HashMap<>();
    private static Map<String, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EHouseInfoOrigin c : EHouseInfoOrigin.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EHouseInfoOrigin query(int status) {
        return grabOrderStatusMap.get(status);
    }

    EHouseInfoOrigin(String type, String label) {
        this.status = type;
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EHouseInfoOrigin inspectionOrderStatus) {
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
