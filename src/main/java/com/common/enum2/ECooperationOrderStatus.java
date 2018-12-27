package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
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
public enum ECooperationOrderStatus implements IEnumCodeName {
    requested(1, "待确认"), accept(2, "同意"), refuse(4, "拒绝"),  /*Complete(3, "已关闭"),*/ Cancel(5, "已取消");
    private int status;
    private String label;
    private static Map<Integer, ECooperationOrderStatus> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (ECooperationOrderStatus c : ECooperationOrderStatus.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static ECooperationOrderStatus query(int status) {
        return grabOrderStatusMap.get(status);
    }

    ECooperationOrderStatus(int status, String label) {
        this.status = status;
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

    public boolean equals(ECooperationOrderStatus inspectionOrderStatus) {
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
        return Constant2.Order_type_Label_COOPERATION_ORDER;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
