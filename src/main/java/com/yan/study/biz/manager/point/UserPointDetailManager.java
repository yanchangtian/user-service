package com.yan.study.biz.manager.point;

import com.yan.study.biz.dao.point.entity.UserPointDetailDO;

import java.util.List;

public interface UserPointDetailManager {

    void insert(UserPointDetailDO userPointDetail);

    UserPointDetailDO queryByDetailCode(String detailCode);

    int update(UserPointDetailDO userPointDetail);

    /**
     * 查询用户可用积分记录 detailStatus为已领取的记录
     *
     * @param userId 精灵id
     * @param pointType 积分类型
     * @return 可用积分记录
     */
    List<UserPointDetailDO> queryAvailablePointRecord(String userId, String pointType);

}
