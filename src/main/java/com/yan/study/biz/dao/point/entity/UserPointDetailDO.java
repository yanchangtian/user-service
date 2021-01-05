package com.yan.study.biz.dao.point.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户积分明细表
 */
@Data
public class UserPointDetailDO {

    private Long id;

    private String userId;

    private String pointType;

    private String detailCode;

    private String giveReason;
    /**
     * 幂等id
     */
    private String idempotentId;

    // ----- 积分数据 -----
    /**
     * 发放积分
     */
    private Long givePoints;
    /**
     * 可用积分(领取的积分/直接发放的积分)
     */
    private Long availablePoints;
    /**
     * 已消耗积分
     */
    private Long consumedPoints;
    /**
     * 已冻结积分
     */
    private Long freezePoints;
    /**
     * 已过期积分
     */
    private Long expiredPoints;
    /**
     * 合并积分
     */
    private Long mergedPoints;

    // ----- 时间数据 -----
    /**
     * 预发放时间
     */
    private Date preGiveTime;
    /**
     * 生效时间(领取时间/直接发放时间)
     */
    private Date effectTime;
    /**
     * 失效时间(预发放超时未领取)
     */
    private Date invalidTime;
    /**
     * 过期时间(超时未使用)
     */
    private Date expireTime;
    /**
     * 合并时间
     */
    private Date mergedTime;
    /**
     * 积分明细状态
     */
    private String detailStatus;
    /**
     * 版本号
     */
    private Long version;

}
