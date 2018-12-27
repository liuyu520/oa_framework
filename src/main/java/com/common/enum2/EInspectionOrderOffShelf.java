package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 验房订单
 */
public enum EInspectionOrderOffShelf {
    //1:上架即在售中;2:下架,客户看不到
    onsale(1, "在售中", "上架"), offShelf(2, "已下架", "已下架");
    private int status;
    private String label;
    /***
     * 动作
     */
    private String action;
    private static Map<Integer, EInspectionOrderOffShelf> visitOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EInspectionOrderOffShelf c : EInspectionOrderOffShelf.values()) {
            visitOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    EInspectionOrderOffShelf(int type, String label, String action) {
        this.status = type;
        this.label = label;
        this.action = action;
    }

    public boolean equals(EInspectionOrderOffShelf inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static EInspectionOrderOffShelf query(int status) {
        return visitOrderStatusMap.get(status);
    }

    public String getAction() {
        return action;
    }

    public int getStatus() {
        return status;
    }

    public static Map<Integer, EInspectionOrderOffShelf> getVisitOrderStatusMap() {
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
