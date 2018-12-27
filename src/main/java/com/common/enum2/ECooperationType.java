package com.common.enum2;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理类型:1-先抢先得,2-独家代理,3-指定经纪人;4-房主自己发布<br />
 * ;25-独家代理给门店;;<br />35-代理出售给门店
 * 前端选择"独家代理",只能选择一个经纪人;<br />
 * 选择代理,就会进入下一个界面:有两个选项:先抢先得,指定经纪人
 */
public enum ECooperationType {
    common(0, "没有对接合作"), Customer_secondAgent(1, "我的客源"), House_primaryAgent(2, "我的房源");
    private int type;
    private String label;

    ECooperationType(int type, String label) {
        this.type = type;
        this.label = label;
    }

    private static Map<Integer, ECooperationType> grabOrderStatusMap = new HashMap<>();

    static {
        for (ECooperationType c : ECooperationType.values()) {
            grabOrderStatusMap.put(c.getType(), c);
        }
    }

    public static ECooperationType query(int type) {
        return grabOrderStatusMap.get(type);
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean equals(ECooperationType inspectionOrderStatus) {
        return this.compareTo(inspectionOrderStatus) == 0;
    }
}
