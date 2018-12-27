package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public enum EHouseInfoType implements IEnumCodeName {
    AgentCreate(1, "发布前(经纪人发布)"), HouseOwnerCreateBeforeAPPOINT(2, "房主指派或抢单"), HouseOwnerCreateSelfEmployed(4, "房主自主发布(不会分配给经纪人)");
    private int status;
    private String label;
    private static Map<Integer, EHouseInfoType> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EHouseInfoType c : EHouseInfoType.values()) {
            grabOrderStatusMap.put(c.getStatus(), c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EHouseInfoType query(Integer status) {
        return grabOrderStatusMap.get(status);
    }

    EHouseInfoType(Integer type, String label) {
        this.status = type;
        this.label = label;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EHouseInfoType inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    /***
     * 打印枚举类所有状态含义
     * @return
     */
    public static String getAllEnumDoc() {
        return Joiner.on(SystemHWUtil.CRLF).withKeyValueSeparator(":").join(orderStatusLabelMap);
    }

    @Override
    public String getTypeLabel() {
        return Constant2.Order_type_Label_houseInfo;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
