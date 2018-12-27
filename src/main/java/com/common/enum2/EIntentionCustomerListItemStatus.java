package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 1:最近回访;3:购房中,注意:和IntentionCustomer 中的Status是两码事
 */
public enum EIntentionCustomerListItemStatus {
    followUpVisit(1, "最近回访"), hasPurchasedWaiting(3, "购房中");
    private int status;
    private String label;
    private static Map<Integer, EIntentionCustomerListItemStatus> cTypeMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EIntentionCustomerListItemStatus c : EIntentionCustomerListItemStatus.values()) {
            cTypeMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EIntentionCustomerListItemStatus query(int status) {
        return cTypeMap.get(status);
    }

    EIntentionCustomerListItemStatus(int type, String label) {
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

    public boolean equals(EIntentionCustomerListItemStatus inspectionOrderStatus) {
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
