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
public enum EApplyForCancelOrderStatus {
    applyFor(1, "开始申请"), processing(2, "处理中"), agree(4, "同意退单"), refuse(5, "拒绝退单"), Cancel(6, "已取消");
    private int status;
    private String label;
    private static Map<Integer, EApplyForCancelOrderStatus> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EApplyForCancelOrderStatus c : EApplyForCancelOrderStatus.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EApplyForCancelOrderStatus query(int status) {
        return grabOrderStatusMap.get(status);
    }

    EApplyForCancelOrderStatus(int type, String label) {
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

    public boolean equals(EApplyForCancelOrderStatus inspectionOrderStatus) {
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
