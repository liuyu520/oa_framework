package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * 房源状态  <br />
 * 1:正常注册;2:经纪人发布房源时,自动生成
 */
public enum EUserRegisterType implements IEnumCodeName {
    NORMAL_REGISTER(1, "正常注册"), AGENT_CREATE_HOUSE(2, "经纪人发布房源时,自动生成");
    private static Map<Integer, EUserRegisterType> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EUserRegisterType c : EUserRegisterType.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    private int status;
    private String label;

    EUserRegisterType(int type, String label) {
        this.status = type;
        this.label = label;
    }

    public static EUserRegisterType query(int status) {
        return grabOrderStatusMap.get(status);
    }

    /***
     * 打印枚举类所有状态含义
     * @return
     */
    public static String getAllEnumDoc() {
        return Joiner.on(SystemHWUtil.CRLF).withKeyValueSeparator(":").join(orderStatusLabelMap);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EUserRegisterType inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
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
