package com.yan.study.biz.manager.point;

import com.yan.study.biz.common.BaseResult;

public interface UserPointBizManager {

    /**
     * 预发放积分
     *
     * @param userId 精灵id
     * @param pointType 积分类型
     * @param points 积分数量
     * @param idempotentId 幂等id
     * @param reason 发放理由
     * @return 明细code
     */
    BaseResult<String> preGivePoints(String userId, String pointType, Long points, String idempotentId, String reason);

    /**
     * 领取积分
     *
     * @param userId 精灵id
     * @param pointType 积分类型
     * @param detailCode 明细code
     * @return 空
     */
    BaseResult<Void> receivePoints(String userId, String pointType, String detailCode);

    /**
     * 直接发放积分
     *
     * @param userId 精灵id
     * @param pointType 积分类型
     * @param points 积分数
     * @param idempotentId 幂等id
     * @param reason 发放理由
     * @return 空
     */
    BaseResult<Void> givePoints(String userId, String pointType, Long points, String idempotentId, String reason);

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
