package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 验房订单
 */
public enum EInspectionOrderStatus implements IEnumCodeName {
    //1-待接单，2-待看房,3-看房中,4-完成待确认,5-完成,6-订单已取消(所有取消类别均包含),7-拒单,8-待评价
    created(1, "待接单", "请求验房"), accept(2, "待验房", "接单"), goAhead(3, "前往中", "发出"), ing(4, "验房中", "验房"), toConfirm(5, "待确认", "验房结束"), toPublish(6, "待发布", "房主确认"), beforeVideo(7, "发布中", "只剩下视频链接没有上传"), complete(8, "已完成", "已评价"), cancel(10, "已取消", "取消"/*取消委托*/), refuse(11, "拒单", "拒单"),
    /*offShelf(12, "下架", "经纪人下架"),*/ closed(14, "关闭", "房源已经售出");
    private int status;
    private String label;
    /***
     * 动作
     */
    private String action;
    private static Map<Integer, EInspectionOrderStatus> visitOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EInspectionOrderStatus c : EInspectionOrderStatus.values()) {
            visitOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    /***
     * TODO
     * @param currentStatus
     * @return
     */
    public static EInspectionOrderStatus nextStatus(int currentStatus) {
        currentStatus++;
        EInspectionOrderStatus inspectionOrderStatus = query(currentStatus++);
        if (null == inspectionOrderStatus) {
            inspectionOrderStatus = query(currentStatus++);
        }
        if (null == inspectionOrderStatus) {
            inspectionOrderStatus = query(currentStatus++);
        }
        return inspectionOrderStatus;
    }

    public static Integer[] getWaitingStatus() {
        return new Integer[]{EInspectionOrderStatus.created.getStatus(),//故意去掉注释的
                EInspectionOrderStatus.accept.getStatus(),
                EInspectionOrderStatus.goAhead.getStatus(),
                EInspectionOrderStatus.ing.getStatus(),
                EInspectionOrderStatus.toConfirm.getStatus(),
                EInspectionOrderStatus.toPublish.getStatus(),
                EInspectionOrderStatus.beforeVideo.getStatus()
        };
    }

    public static Integer[] getCompleteStatus() {
        return new Integer[]{EInspectionOrderStatus.cancel.getStatus(),
                EInspectionOrderStatus.refuse.getStatus(),
//                EInspectionOrderStatus.offShelf.getStatus(),
                EInspectionOrderStatus.complete.getStatus()
        };
    }

    EInspectionOrderStatus(int type, String label, String action) {
        this.status = type;
        this.label = label;
        this.action = action;
    }

    public boolean equals(EInspectionOrderStatus inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static EInspectionOrderStatus query(int status) {
        return visitOrderStatusMap.get(status);
    }

    public String getAction() {
        return action;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public static Map<Integer, EInspectionOrderStatus> getVisitOrderStatusMap() {
        return visitOrderStatusMap;
    }

    /***
     * 打印枚举类所有状态含义
     * @return
     */
    public static String getAllEnumDoc() {
        return Joiner.on(SystemHWUtil.CRLF).withKeyValueSeparator(":").join(orderStatusLabelMap);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getTypeLabel() {
        return Constant2.Order_type_Label_INSPECTION_ORDER;
    }
}
