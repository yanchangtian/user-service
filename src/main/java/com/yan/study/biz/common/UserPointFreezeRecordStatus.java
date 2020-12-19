package com.yan.study.biz.common;

public enum UserPointFreezeRecordStatus {

    FREEZED("已冻结"),

    UNFROZE("已解冻"),

    CONSUMED("已消耗");

    private final String status;

    UserPointFreezeRecordStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}

