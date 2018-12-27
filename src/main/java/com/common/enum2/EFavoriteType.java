package com.common.enum2;

import com.common.util.SystemHWUtil;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public enum EFavoriteType {
    toSeeList(1, "待看清单"), Favorite(3, "收藏夹");
    private int status;
    private String label;
    private static Map<Integer, EFavoriteType> favoriteTypeMap = new HashMap<>();
    private static Map<Integer, String> orderStatusLabelMap = new HashMap<>();

    static {
        for (EFavoriteType c : EFavoriteType.values()) {
            favoriteTypeMap.put(c.status, c);
            orderStatusLabelMap.put(c.status, c.toString());
        }
    }

    public static EFavoriteType query(int status) {
        return favoriteTypeMap.get(status);
    }

    EFavoriteType(int type, String label) {
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

    public boolean equals(EFavoriteType inspectionOrderStatus) {
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
