package com.yan.study.biz.dao.point.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户积分账户表
 */
@Data
public class UserPointAccountDO {

    private Long id;

    private String userId;

    private String pointType;
    /**
     * 可用积分
     */
    private Long availablePoint;
    /**
     * 可领取积分
     */
    private Long receivedPoint;
    /**
     * 冻结积分
     */
    private Long freezePoint;
    /**
     * 已消耗积分
     */
    private Long consumedPoint;
    /**
     * 已过期积分
     */
    private Long expiredPoint;
    /**
     * 上次更新过期积分的时间
     */
    private Date lastCalcExpireTime;
    /**
     * 版本号
     */
    private Long version;

}
