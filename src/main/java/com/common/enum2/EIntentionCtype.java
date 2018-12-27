package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/***
 * 和 ECTypeType 冗余
 */
public enum EIntentionCtype {
    houseOwner(1, "房主"), buyer(2, "买房者");
    private int status;
    private String label;
    private static Map<Integer, EIntentionCtype> payTypeMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EIntentionCtype c : EIntentionCtype.values()) {
            payTypeMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EIntentionCtype query(int status) {
        return payTypeMap.get(status);
    }

    EIntentionCtype(int type, String label) {
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

    public boolean equals(EIntentionCtype inspectionOrderStatus) {
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
