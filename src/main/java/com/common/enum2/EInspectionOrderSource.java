package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 验房订单
 */
public enum EInspectionOrderSource {
    //1-待接单，2-待看房,3-看房中,4-完成待确认,5-完成,6-订单已取消(所有取消类别均包含),7-拒单,8-待评价
    AgentCreate(1, "经纪人直接替房主发布房源"), HouseOwnerCreateBeforeAPPOINT(2, "房主指派"), grab(3, "抢单"), HouseOwnerCreateSelfEmployed(4, "房主自己发布,没有找经纪人代理");
    private int status;
    private String label;

    private static Map<Integer, EInspectionOrderSource> visitOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EInspectionOrderSource c : EInspectionOrderSource.values()) {
            visitOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    EInspectionOrderSource(int type, String label) {
        this.status = type;
        this.label = label;
    }

    public boolean equals(EInspectionOrderSource inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static EInspectionOrderSource query(int status) {
        return visitOrderStatusMap.get(status);
    }


    public int getStatus() {
        return status;
    }

    public static Map<Integer, EInspectionOrderSource> getVisitOrderStatusMap() {
        return visitOrderStatusMap;
    }

    /***
     * 打印枚举类所有状态含义
     * @return
     */
    public static String getAllEnumDoc() {
        return Joiner.on(SystemHWUtil.CRLF).withKeyValueSeparator(":").join(orderStatusLabelMap);
    }
}
