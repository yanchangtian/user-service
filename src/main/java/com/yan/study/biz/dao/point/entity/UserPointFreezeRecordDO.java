package com.yan.study.biz.dao.point.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserPointFreezeRecordDO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 精灵id
     */
    private String userId;
    /**
     * 积分类型
     */
    private String pointType;
    /**
     * 冻结code
     */
    private String freezeCode;
    /**
     * 幂等id
     */
    private String idempotentId;
    /**
     * 冻结原因
     */
    private String freezeReason;
    /**
     * 冻结积分数量
     */
    private Long freezePoints;
    /**
     * 冻结时间
     */
    private Date freezeTime;
    /**
     * 冻结状态
     *
     * @see com.yan.study.biz.common.UserPointFreezeRecordStatus
     */
    private String freezeRecordStatus;
    /**
     * 版本号
     */
    private Long version;

}
