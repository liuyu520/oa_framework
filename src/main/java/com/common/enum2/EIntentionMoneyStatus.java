package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * Grabbing:待抢单;<br />
 * APPOINTing:未完成;<br />
 * Complete:关闭<br />
 * Cancel:取消
 */
public enum EIntentionMoneyStatus {
    toBePaid(1, "待支付"), startingPay(2, "发起支付,不一定会支付"), Paid(4, "已支付"/*(派单派一部分)*/), Complete(5, "已关闭"), Cancel(6, "已取消");
    private int status;
    private String label;
    private static Map<Integer, EIntentionMoneyStatus> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EIntentionMoneyStatus c : EIntentionMoneyStatus.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EIntentionMoneyStatus query(int status) {
        return grabOrderStatusMap.get(status);
    }

    EIntentionMoneyStatus(int type, String label) {
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

    public boolean equals(EIntentionMoneyStatus inspectionOrderStatus) {
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
