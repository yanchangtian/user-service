package com.yan.study.biz.dao.point.entity;

import lombok.Data;

@Data
public class UserPointFreezeRecordDetailDO {

    private Long id;

    private String userId;

    private String pointType;

    private String freezeCode;

    private String detailCode;

    private Long freezePoints;

    private Long version;

}
