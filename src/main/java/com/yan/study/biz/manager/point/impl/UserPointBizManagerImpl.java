package com.yan.study.biz.manager.point.impl;

import com.yan.study.biz.common.BaseResult;
import com.yan.study.biz.common.PointSystemException;
import com.yan.study.biz.common.UserPointDetailStatus;
import com.yan.study.biz.dao.point.entity.UserPointAccountDO;
import com.yan.study.biz.dao.point.entity.UserPointDetailDO;
import com.yan.study.biz.manager.point.UserPointAccountManager;
import com.yan.study.biz.manager.point.UserPointBizManager;
import com.yan.study.biz.manager.point.UserPointDetailManager;

import javax.annotation.Resource;
import java.util.Date;

public class UserPointBizManagerImpl implements UserPointBizManager {

    @Resource
    private UserPointAccountManager userPointAccountManager;
    @Resource
    private UserPointDetailManager userPointDetailManager;

    @Override
    // 事务标签
    public BaseResult<String> preGivePoint(String userId, String pointType, Long points, String idem, String reason) {
        try {
            String code = "";
            // 1. 用户账户信息
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            userPointAccount.setReceivedPoint(userPointAccount.getReceivedPoint() + points);
            Integer i = userPointAccountManager.updateAccount(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新用户账户");
            }
            // 2. 用户积分明细信息
            UserPointDetailDO userPointDetail = new UserPointDetailDO();
            userPointDetail.setUserId(userId);
            userPointDetail.setPointType(pointType);
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVABLE.getStatus());
            userPointDetail.setGivePoints(points);
            userPointDetail.setPreGiveTime(new Date());

            return BaseResult.success(code);
        } catch (Exception e) {
            return BaseResult.fail("预发放积分失败");
        }
    }
}