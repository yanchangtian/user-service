package com.yan.study.biz.manager.point;

import com.yan.study.biz.dao.point.entity.UserPointDetailDO;

import java.util.List;

public interface UserPointDetailManager {

    void insert(UserPointDetailDO userPointDetail);

    UserPointDetailDO queryByDetailCode(String detailCode);

    int update(UserPointDetailDO userPointDetail);

    /**
     * 查询用户可用积分记录, detailStatus为 已领取 和 部分冻结 的记录, 按获取时间升序
     *
     * @param userId 精灵id
     * @param pointType 积分类型
     * @return 可用积分记录
     */
    List<UserPointDetailDO> queryAvailablePointRecord(String userId, String pointType);

    /**
     * 部分冻结积分详情
     *
     * @param userPointDetail 积分详情
     * @param points 冻结积分数量
     */
    void freezeDetailWithPortion(UserPointDetailDO userPointDetail, Long points);

    /**
     * 全部冻结积分详情
     *
     * @param allFreezeList 积分集合
     */
    void freezeDetailWithAll(List<UserPointDetailDO> allFreezeList);

    /**
     *
     *
     * @param idempotentId
     * @return
     */
    UserPointDetailDO queryByUserIdAndIdempotentId(String userId, String idempotentId);

}
