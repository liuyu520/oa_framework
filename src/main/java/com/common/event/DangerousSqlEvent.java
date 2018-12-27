package com.common.event;

/***
 * 执行了危险的SQL 操作,例如:DELETE 语句
 */
public class DangerousSqlEvent {
    private String sql;
    private String exeTime;

    public String getSql() {
        return sql;
    }

    public String getExeTime() {
        return exeTime;
    }

    public DangerousSqlEvent setExeTime(String exeTime) {
        this.exeTime = exeTime;
        return this;
    }

    public DangerousSqlEvent setSql(String sql) {
        this.sql = sql;
        return this;
    }
}
