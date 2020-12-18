package com.yan.study.biz.common;

public enum ValidStatus {

    /**
     * 删除
     */
    DELETE(0),
    /**
     * 有效
     */
    VALID(1),
    /**
     * 无效
     */
    INVALID(2);

    private final Integer status;

    ValidStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

}
