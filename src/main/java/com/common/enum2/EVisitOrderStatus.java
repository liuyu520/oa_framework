package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 客户确认之后,就是待评价
 */
public enum EVisitOrderStatus implements IEnumCodeName {
    //1-待接单，2-待看房,3-看房中,4-完成待确认,5-完成,6-订单已取消(所有取消类别均包含),7-拒单,8-待评价
    requested(1, "待接单", "请求带看"), accept(2, "待看房", "接单"), goAhead(3, "前往中", "发出"), visiting(4, "看房中", "前往看房"), toConfirm(5, "完成待确认", "看房结束")/*, complete(6, "完成", "确认")*/, cancel(8, "已取消", "取消"), refuse(9, "已拒单", "拒单"), toEvaluate(10, "待评价", "待评价"/* 买卖端调用,即客户确认 */), completeAfterEvaluate(11, "完成", "评价之后")/* 评价之后 */;
    private int status;
    private String label;
    /***
     * 动作
     */
    private String action;

    /***
     * 待评价属于为完成,2周之内不评价,则自动好评
     * @return
     */
    public static Integer[] getWaitingStatus() {
        return new Integer[]{EVisitOrderStatus.requested.getStatus(),
                EVisitOrderStatus.accept.getStatus(),
                EVisitOrderStatus.goAhead.getStatus(),
                EVisitOrderStatus.visiting.getStatus(),
                EVisitOrderStatus.toConfirm.getStatus(),
                EVisitOrderStatus.toEvaluate.getStatus()
        };
    }

    public static Integer[] getCompleteStatus() {
        return new Integer[]{EVisitOrderStatus.cancel.getStatus(),
                EVisitOrderStatus.refuse.getStatus(),
                EVisitOrderStatus.completeAfterEvaluate.getStatus()
        };
    }

    private static Map<Integer, EVisitOrderStatus> visitOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EVisitOrderStatus c : EVisitOrderStatus.values()) {
            visitOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    EVisitOrderStatus(int type, String label, String action) {
        this.status = type;
        this.label = label;
        this.action = action;
    }

    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public static EVisitOrderStatus query(int status) {
        /*for (EVisitOrderStatus c : EVisitOrderStatus.values()) {
            if (c.getStatus() == status) {
                return c;
            }
        }*/
        return visitOrderStatusMap.get(status);
//        return requested;
    }

    public String getAction() {
        return action;
    }

    public int getStatus() {
        return status;
    }

    public static Map<Integer, EVisitOrderStatus> getVisitOrderStatusMap() {
        return visitOrderStatusMap;
    }

    public boolean equals(EVisitOrderStatus inspectionOrderStatus) {
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
        return Constant2.Order_type_Label_VISIT_ORDER;
    }
}
