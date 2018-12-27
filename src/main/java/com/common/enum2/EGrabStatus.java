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
 * Cancel:取消 <br />
 * 如果是抢单(随机),则状态为 Grabbing(1, "待接单"),
 * 如果是指派,或独家代理,则(2, "未完成")
 * 对于抢单:抢单完成的经纪人 数量达标,则状态变为 关闭
 * 对于指派,如果需要3个经纪人,只有一个经纪人接单,状态保持不变,仍然是(2, "未完成"),
 * 如果都接单或拒单,则状态变为 关闭
 */
public enum EGrabStatus implements IEnumCodeName {
    Grabbing(1, "待接单"), APPOINTing(2, "派单中"/*待接单(派单派一部分)*/), Complete(3, "已关闭"), Cancel(4, "已取消");
    private int status;
    private String label;
    private static Map<Integer, EGrabStatus> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EGrabStatus c : EGrabStatus.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EGrabStatus query(int status) {
        return grabOrderStatusMap.get(status);
    }

    EGrabStatus(int type, String label) {
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

    public boolean equals(EGrabStatus inspectionOrderStatus) {
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
        return Constant2.Order_type_Label_GRAB_ORDER;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
