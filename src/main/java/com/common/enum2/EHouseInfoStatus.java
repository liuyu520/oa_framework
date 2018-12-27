package com.common.enum2;

import com.common.dict.Constant2;
import com.common.enum2.intef.IEnumCodeName;
import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * 房源状态  <br />
 * 买卖端 点击预览,状态变为"待发布"
 */
public enum EHouseInfoStatus implements IEnumCodeName {
    AgentCreate(1, "发布前(经纪人发布)"), HouseOwnerCreateSelfEmployed(2, "房主自主发布(不会分配给经纪人)"), HouseOwnerCreateBeforeAPPOINT(3, "房主发布(还没有分配给经纪人)"), toBeReleased(5, "待发布"), pendingRelease(6, "发布中")/* 评论完成,状态变为发布中 */, PendingAudit(7, "审核中"), sale(8, "在售"), Book(9, "已定"), beBought(10, "已售"),
    off(11, "下架"),
    releaseFailed(12, "上传失败"),
    AuditFailed(13, "审核失败");
    private int status;
    private String label;
    private static Map<Integer, EHouseInfoStatus> grabOrderStatusMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EHouseInfoStatus c : EHouseInfoStatus.values()) {
            grabOrderStatusMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EHouseInfoStatus query(int status) {
        return grabOrderStatusMap.get(status);
    }

    EHouseInfoStatus(int type, String label) {
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

    public boolean equals(EHouseInfoStatus inspectionOrderStatus) {
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
        return Constant2.Order_type_Label_houseInfo;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
