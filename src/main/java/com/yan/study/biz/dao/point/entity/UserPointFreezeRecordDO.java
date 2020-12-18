package com.yan.study.biz.dao.point.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserPointFreezeRecordDO {

    private Long id;

    private String userId;

    private String pointType;

    private String freezeCode;

    private String transactionId;

    private String freezeReason;

    private Long freezePoints;

    private Date freezeTime;

    private String freezeRecordStatus;

    private Long version;

}
