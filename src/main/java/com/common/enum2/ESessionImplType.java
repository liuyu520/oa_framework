package com.common.enum2;

import com.common.enu.IEnumStrCode;

/**
 * session共享的实现方式. <br />
 *
 * @author huangweii
 * @since 2018/10/13
 */
public enum ESessionImplType implements IEnumStrCode {
    SESSION_IMPL_TYPE_DB("db", "使用数据库"), SESSION_IMPL_TYPE_REDIS("redis", "使用redis"), SESSION_IMPL_TYPE_ZOOKEEPER("zookeeper", "使用zookeeper");

    ESessionImplType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    private String code;
    private String displayName;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }
}
