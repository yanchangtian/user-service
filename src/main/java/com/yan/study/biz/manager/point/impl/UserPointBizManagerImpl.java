package com.yan.study.biz.manager.point.impl;

import com.yan.study.biz.common.BaseResult;
import com.yan.study.biz.common.PointSystemException;
import com.yan.study.biz.common.UserPointDetailStatus;
import com.yan.study.biz.common.UserPointFreezeRecordStatus;
import com.yan.study.biz.dao.point.entity.UserPointAccountDO;
import com.yan.study.biz.dao.point.entity.UserPointDetailDO;
import com.yan.study.biz.dao.point.entity.UserPointFreezeRecordDO;
import com.yan.study.biz.dao.point.entity.UserPointFreezeRecordDetailDO;
import com.yan.study.biz.manager.point.*;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

public class UserPointBizManagerImpl implements UserPointBizManager {

    @Resource
    private UserPointAccountManager userPointAccountManager;
    @Resource
    private UserPointDetailManager userPointDetailManager;
    @Resource
    private UserPointFreezeRecordManager userPointFreezeRecordManager;
    @Resource
    private UserPointFreezeRecordDetailManager userPointFreezeRecordDetailManager;

    @Override
    // 事务标签
    public BaseResult<String> preGivePoints(String userId, String pointType, Long points, String idempotentId, String reason) {
        try {
            // 0.积分类型校验

            String code = "";
            // 1.用户账户信息
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            userPointAccount.setReceivedPoint(userPointAccount.getReceivedPoint() + points);
            Integer i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新用户账户");
            }
            // 2.用户积分明细信息
            UserPointDetailDO userPointDetail = new UserPointDetailDO();
            userPointDetail.setUserId(userId);
            userPointDetail.setPointType(pointType);
            userPointDetail.setDetailCode(UUID.randomUUID().toString());
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVABLE.getStatus());
            userPointDetail.setGivePoints(points);
            userPointDetail.setPreGiveTime(new Date());
            userPointDetail.setGiveReason(reason);
            userPointDetailManager.insert(userPointDetail);

            return BaseResult.success(code);
        } catch (Exception e) {
            return BaseResult.fail("预发放积分失败");
        }
    }

    @Override
    public BaseResult<Void> receivePoints(String userId, String pointType, String detailCode) {
        try {
            // 1.更新明细表
            UserPointDetailDO userPointDetail = userPointDetailManager.queryByDetailCode(detailCode);
            if (userPointDetail == null) {
                return BaseResult.fail("无效的明细code");
            }
            if (UserPointDetailStatus.RECEIVABLE.getStatus().equals(userPointDetail.getDetailStatus())) {
                return BaseResult.fail("无效的明细code");
            }
            userPointDetail.setAvailablePoints(userPointDetail.getGivePoints());
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
            userPointDetail.setEffectTime(new Date());
            int i = userPointDetailManager.update(userPointDetail);
            if (i < 1) {
                throw new PointSystemException("并发修改积分明细");
            }

            // 2. 更新账户表
            UserPointAccountDO userPointAccount = userPointAccountManager.query(userId, pointType);
            if (userPointAccount == null) {
                throw new PointSystemException("");
            }
            userPointAccount.setAvailablePoint(userPointAccount.getAvailablePoint() + userPointDetail.getGivePoints());
            userPointAccount.setReceivedPoint(userPointAccount.getReceivedPoint() - userPointDetail.getGivePoints());
            i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发修改积分账户");
            }

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("领取积分失败");
        }
    }

    @Override
    public BaseResult<Void> givePoints(String userId, String pointType, Long points, String idempotentId, String reason) {
        try {
            // 0.1 幂等判断


            // 1.更新账户
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            userPointAccount.setAvailablePoint(userPointAccount.getAvailablePoint() + points);
            int i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新积分账户");
            }
            // 2.创建积分明细
            UserPointDetailDO userPointDetail = new UserPointDetailDO();
            userPointDetail.setUserId(userId);
            userPointDetail.setPointType(pointType);
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
            userPointDetail.setGivePoints(points);
            userPointDetail.setAvailablePoints(points);
            userPointDetail.setEffectTime(new Date());
            userPointDetail.setIdempotentId(idempotentId);
            userPointDetailManager.insert(userPointDetail);

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("直接发放积分失败");
        }
    }

    @Override
    public BaseResult<String> freezePoints(String userId, String pointType, Long points, String idempotentId, String reason) {
        try {
            // 1.更新用户账户
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            if (userPointAccount.getAvailablePoint() < points) {
                return BaseResult.fail("可用积分不足");
            }
            userPointAccount.setAvailablePoint(userPointAccount.getAvailablePoint() - points);
            userPointAccount.setFreezePoint(userPointAccount.getFreezePoint() + points);
            int i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新用户账户");
            }

            // 2.查询积分明细
            List<UserPointDetailDO> userPointDetailList = userPointDetailManager.queryAvailablePointRecord(userId, pointType);

            // 3.冻结积分明细

            // 3.1 全部冻结列表
            List<UserPointDetailDO> allFreezeList = new ArrayList<>();
            // K detailCode V points
            Map<String, Long> detailCodeMap = new HashMap<>();
            Long needFreezePoints = points;

            for (UserPointDetailDO userPointDetail : userPointDetailList) {
                Long availablePoints = userPointDetail.getAvailablePoints();
                if (needFreezePoints < availablePoints) {
                    // 部分冻结
                    userPointDetailManager.freezeDetailWithPortion(userPointDetail, needFreezePoints);
                    detailCodeMap.put(userPointDetail.getDetailCode(), needFreezePoints);
                    break;
                } else if (needFreezePoints.equals(availablePoints)) {
                    // 全部冻结
                    allFreezeList.add(userPointDetail);
                    detailCodeMap.put(userPointDetail.getDetailCode(), needFreezePoints);
                    break;
                } else {
                    needFreezePoints = needFreezePoints - availablePoints;
                    allFreezeList.add(userPointDetail);
                    detailCodeMap.put(userPointDetail.getDetailCode(), availablePoints);
                }
            }

            // 3.2 冻结全部冻结列表
            if (!CollectionUtils.isEmpty(allFreezeList)) {
                userPointDetailManager.freezeDetailWithAll(allFreezeList);
            }
            // 4. 新增冻结记录
            UserPointFreezeRecordDO userPointFreezeRecord = new UserPointFreezeRecordDO();
            userPointFreezeRecord.setUserId(userId);
            userPointFreezeRecord.setPointType(pointType);
            userPointFreezeRecord.setFreezeCode(UUID.randomUUID().toString());
            userPointFreezeRecord.setIdempotentId(idempotentId);
            userPointFreezeRecord.setFreezeReason(reason);
            userPointFreezeRecord.setFreezePoints(points);
            userPointFreezeRecord.setFreezeTime(new Date());
            userPointFreezeRecord.setFreezeRecordStatus(UserPointFreezeRecordStatus.FREEZED.getStatus());
            userPointFreezeRecordManager.insert(userPointFreezeRecord);

            // 5. 新增冻结明细记录
            for (String detailCode : detailCodeMap.keySet()) {
                UserPointFreezeRecordDetailDO userPointFreezeRecordDetail = new UserPointFreezeRecordDetailDO();
                userPointFreezeRecordDetail.setUserId(userId);
                userPointFreezeRecordDetail.setPointType(pointType);
                userPointFreezeRecordDetail.setDetailCode();
                userPointFreezeRecordDetail.setFreezeCode();
                userPointFreezeRecordDetail.setFreezePoints(detailCodeMap.get(detailCode));

            }

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("冻结失败");
        }
    }


}