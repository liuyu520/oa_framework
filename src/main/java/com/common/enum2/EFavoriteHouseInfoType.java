package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public enum EFavoriteHouseInfoType {
    Agent(3, "经纪人代理"), HouseOwnerCreateSelfEmployed(4, "房主自主发布(不会分配给经纪人)");
    private int status;
    private String label;
    private static Map<Integer, EFavoriteHouseInfoType> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EFavoriteHouseInfoType c : EFavoriteHouseInfoType.values()) {
            grabOrderStatusMap.put(c.getStatus(), c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EFavoriteHouseInfoType query(Integer status) {
        return grabOrderStatusMap.get(status);
    }

    EFavoriteHouseInfoType(Integer type, String label) {
        this.status = type;
        this.label = label;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EFavoriteHouseInfoType inspectionOrderStatus) {
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
