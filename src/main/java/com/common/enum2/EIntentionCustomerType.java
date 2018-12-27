package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * C,指Customer,即Customer type
 */
public enum EIntentionCustomerType {
    noOrder(1, "意向客户"), hasOrdered(3, "通过订单添加的客户");
    private int status;
    private String label;
    private static Map<Integer, EIntentionCustomerType> intentionCustomerTypeMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EIntentionCustomerType c : EIntentionCustomerType.values()) {
            intentionCustomerTypeMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EIntentionCustomerType query(int status) {
        return intentionCustomerTypeMap.get(status);
    }

    EIntentionCustomerType(int type, String label) {
        this.status = type;
        this.label = label;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(EIntentionCustomerType inspectionOrderStatus) {
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
