package com.common.event;

import com.common.dao.generic.GenericDao;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class UpdateSingleColumnEvent implements Serializable {
    private int id;
    /***
     * 要更新的字段名称
     */
    private String column;
    /***
     * 实体类
     */
    private Class clz;
    /***
     * 字段的新值
     */
    private String val;

    @JsonIgnore
    private GenericDao genericDao;
    private String requestURI;
    /***
     * 更新之前的值
     */
    private Object oldVal;
    private boolean before = true;

    public UpdateSingleColumnEvent() {
        super();
    }

    public UpdateSingleColumnEvent(int id, String column, Class clz, String val) {
        this.id = id;
        this.column = column;
        this.clz = clz;
        this.val = val;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @JsonIgnore
    public GenericDao getGenericDao() {
        return genericDao;
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public Object getOldVal() {
        return oldVal;
    }

    public void setOldVal(Object oldVal) {
        this.oldVal = oldVal;
    }

    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }
}
