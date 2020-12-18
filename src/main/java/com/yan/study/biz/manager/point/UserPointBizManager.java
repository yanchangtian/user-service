package com.yan.study.biz.manager.point;

import com.yan.study.biz.common.BaseResult;

public interface UserPointBizManager {

    /**
     * 预发放积分
     */
    BaseResult<String> preGivePoint(String userId, String pointType, Long points, String idem, String reason);

    /**
     * 领取积分
     */

    /**
     * 直接发放积分
     */

    /**
     * 冻结积分
     */

    /**
     * 解冻积分
     */

    /**
     * 消耗积分
     */

}
