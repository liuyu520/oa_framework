package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * 实际上少了一个状态<br />
 * 当状态是 待缴意向金时,通过intentionMoney中startPayTime是否为空来判断经纪人是否向客户发送了支付要求<br />
 * pendingNegotiation 也存在两种状态,根据 negotiationTime 是否空来判断,如果为空,则表示刚刚支付完成<br />
 */
public enum EPurchaseHouseOrderStatus implements IEnumCodeName {
    waiting(1, "待接单"), intention2pay(2, "待缴意向金"), pendingNegotiation(3, "待协商"), negotiatedCompleted(4, "协商完成"),
    pendingAppointment(5, "已预约"), pendingContract(6, "待签约"), contracting(7, "签约中"), toConfirm(8, "待确认"), complete(9, "签约完成"), cancel(10, "已取消"), refuse(11, "拒单"), pendingTreatment(12, "待处理"), inProcessing(13, "处理中"), rejectBill(14, "拒绝退单"), returnedAlready(15, "已退单");
    private int status;
    private String label;
    private static Map<Integer, EPurchaseHouseOrderStatus> purchaseOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EPurchaseHouseOrderStatus c : EPurchaseHouseOrderStatus.values()) {
            purchaseOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EPurchaseHouseOrderStatus query(int status) {
        return purchaseOrderStatusMap.get(status);
    }

    public static Integer[] getWaitingStatus() {
        return new Integer[]{EPurchaseHouseOrderStatus.waiting.getStatus(),
                EPurchaseHouseOrderStatus.intention2pay.getStatus(),
                EPurchaseHouseOrderStatus.pendingNegotiation.getStatus(),
                EPurchaseHouseOrderStatus.negotiatedCompleted.getStatus(),
                EPurchaseHouseOrderStatus.pendingAppointment.getStatus(),
                EPurchaseHouseOrderStatus.pendingContract.getStatus(),
                EPurchaseHouseOrderStatus.contracting.getStatus(),
                EPurchaseHouseOrderStatus.toConfirm.getStatus(),
                EPurchaseHouseOrderStatus.pendingTreatment.getStatus(),
                EPurchaseHouseOrderStatus.inProcessing.getStatus()
        };
    }

    public static Integer[] getCompleteStatus() {
        return new Integer[]{EPurchaseHouseOrderStatus.complete.getStatus(),
                EPurchaseHouseOrderStatus.cancel.getStatus(),
                EPurchaseHouseOrderStatus.refuse.getStatus(),
                EPurchaseHouseOrderStatus.rejectBill.getStatus(),
                EPurchaseHouseOrderStatus.returnedAlready.getStatus()
        };
    }

    EPurchaseHouseOrderStatus(int type, String label) {
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

    public boolean equals(EPurchaseHouseOrderStatus inspectionOrderStatus) {
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
        return Constant2.Order_type_Label_PURCHASE_ORDER;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
