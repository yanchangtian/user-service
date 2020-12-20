package com.yan.study.biz.manager.point.impl;

import com.yan.study.biz.common.PointSystemException;
import com.yan.study.biz.common.UserPointDetailStatus;
import com.yan.study.biz.dao.point.entity.UserPointDetailDO;
import com.yan.study.biz.manager.point.UserPointDetailManager;
import java.util.List;

public class UserPointDetailManagerImpl implements UserPointDetailManager {

    @Override
    public void insert(UserPointDetailDO userPointDetail) {

    }

    @Override
    public UserPointDetailDO queryByDetailCode(String detailCode) {
        return null;
    }

    @Override
    public int update(UserPointDetailDO userPointDetail) {
        return 0;
    }

    @Override
    public List<UserPointDetailDO> queryAvailablePointRecord(String userId, String pointType) {
        return null;
    }

    @Override
    // @Transactional
    public void freezeDetailWithAll(List<UserPointDetailDO> allFreezeList) {
        // 必须保证全部成功, 否则抛出异常
        for (UserPointDetailDO userPointDetail : allFreezeList) {
            if (!UserPointDetailStatus.RECEIVABLE.getStatus().equals(userPointDetail.getDetailStatus())
                && !UserPointDetailStatus.PART_FROZEN.getStatus().equals(userPointDetail.getDetailStatus())) {
                throw new PointSystemException("冻结明细状态异常");
            }
            userPointDetail.setFreezePoints(userPointDetail.getFreezePoints() + userPointDetail.getAvailablePoints());
            userPointDetail.setAvailablePoints(0L);
            userPointDetail.setDetailStatus(UserPointDetailStatus.FROZEN.getStatus());
            int i = update(userPointDetail);
            if (i < 1) {
                throw new PointSystemException("并发更新积分详情");
            }
        }

    }

    @Override
    public void freezeDetailWithPortion(UserPointDetailDO userPointDetail, Long points) {
        if (!UserPointDetailStatus.RECEIVABLE.getStatus().equals(userPointDetail.getDetailStatus())
            && !UserPointDetailStatus.PART_FROZEN.getStatus().equals(userPointDetail.getDetailStatus())) {
            throw new PointSystemException("冻结明细状态异常");
        }
        userPointDetail.setFreezePoints(userPointDetail.getFreezePoints() + userPointDetail.getAvailablePoints());
        userPointDetail.setAvailablePoints(0L);
        userPointDetail.setDetailStatus(UserPointDetailStatus.PART_FROZEN.getStatus());
        int i = update(userPointDetail);
        if (i < 1) {
            throw new PointSystemException("并发更新积分详情");
        }
    }

}
