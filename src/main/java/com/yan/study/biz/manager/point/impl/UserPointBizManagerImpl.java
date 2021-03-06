package com.yan.study.biz.manager.point.impl;

import com.yan.study.biz.common.*;
import com.yan.study.biz.dao.point.entity.*;
import com.yan.study.biz.manager.point.*;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

public class UserPointBizManagerImpl implements UserPointBizManager {

    @Resource
    private PointConfigManager pointConfigManager;
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
    public BaseResult<String> preGivePoint(String userId, String pointType, Long points, String idempotentId, String reason) {
        try {
            // 幂等校验

            // 积分类型校验

            // 获取积分类型配置
            PointConfigDO pointConfig = pointConfigManager.getPointConfig(pointType);

            if (!validPointConfig(pointConfig)) {
                return BaseResult.fail("无效的积分类型");
            }

            // 用户积分明细信息
            UserPointDetailDO userPointDetail = new UserPointDetailDO();
            userPointDetail.setUserId(userId);
            userPointDetail.setPointType(pointType);
            userPointDetail.setDetailCode(UUID.randomUUID().toString().replaceAll("-", ""));
            userPointDetail.setIdempotentId(idempotentId);
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVABLE.getStatus());
            userPointDetail.setGivePoints(points);
            userPointDetail.setPreGiveTime(new Date());
            userPointDetail.setInvalidTime(pointConfig.calcPrePointInvalidTime(userPointDetail.getPreGiveTime()));
            userPointDetail.setGiveReason(reason);
            userPointDetailManager.insert(userPointDetail);

            return BaseResult.success(userPointDetail.getDetailCode());
        } catch (Exception e) {
            return BaseResult.fail("预发放积分失败");
        }
    }

    @Override
    public BaseResult<Void> receivePoint(String userId, String pointType, String detailCode) {
        try {
            Date now = new Date();

            // 校验积分类型
            PointConfigDO pointConfig = pointConfigManager.getPointConfig(pointType);

            if (!validPointConfig(pointConfig)) {
                return BaseResult.fail("无效的积分类型");
            }

            // 1. 更新明细表
            UserPointDetailDO userPointDetail = userPointDetailManager.queryByDetailCode(detailCode);
            if (userPointDetail == null) {
                return BaseResult.fail("无效的明细code");
            }
            if (UserPointDetailStatus.RECEIVABLE.getStatus().equals(userPointDetail.getDetailStatus())) {
                return BaseResult.fail("无效的明细code");
            }
            if (now.after(userPointDetail.getInvalidTime())) {
                return BaseResult.fail("已失效, 不可领取");
            }
            userPointDetail.setAvailablePoints(userPointDetail.getGivePoints());
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
            userPointDetail.setEffectTime(new Date());
            userPointDetail.setExpireTime(pointConfig.calcPointExpireTime(userPointDetail.getEffectTime()));
            int i = userPointDetailManager.update(userPointDetail);
            if (i < 1) {
                throw new PointSystemException("并发修改积分明细");
            }

            // 2. 更新账户表
            userPointAccountManager.increasePoints(userId, pointType, userPointDetail.getAvailablePoints());

            // 3. 合并积分
            mergePoint(userId, userPointDetail);

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("领取积分失败");
        }
    }

    @Override
    public BaseResult<Void> givePoint(String userId, String pointType, Long points, String idempotentId, String reason) {
        try {
            // 0.1 幂等判断

            // 校验积分类型
            PointConfigDO pointConfig = pointConfigManager.getPointConfig(pointType);

            if (!validPointConfig(pointConfig)) {
                return BaseResult.fail("无效的积分类型");
            }

            // 2. 创建积分明细
            UserPointDetailDO userPointDetail = new UserPointDetailDO();
            userPointDetail.setUserId(userId);
            userPointDetail.setPointType(pointType);
            userPointDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
            userPointDetail.setGivePoints(points);
            userPointDetail.setAvailablePoints(points);
            userPointDetail.setEffectTime(new Date());
            userPointDetail.setExpireTime(pointConfig.calcPointExpireTime(userPointDetail.getEffectTime()));
            userPointDetail.setIdempotentId(idempotentId);
            userPointDetailManager.insert(userPointDetail);

            // 3. 积分账户表
            userPointAccountManager.increasePoints(userId, pointType, userPointDetail.getAvailablePoints());

            // 4. 合并积分
            mergePoint(userId, userPointDetail );

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("直接发放积分失败");
        }
    }

    @Override
    public BaseResult<String> freezePoint(String userId, String pointType, Long freezePoints, String idempotentId, String reason) {
        try {
            Date now = new Date();
            // 检验幂等id

            // 校验积分类型

            // 1.更新用户账户
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            if (userPointAccount.getAvailablePoint() < freezePoints) {
                return BaseResult.fail("可用积分不足");
            }
            userPointAccount.setAvailablePoint(userPointAccount.getAvailablePoint() - freezePoints);
            userPointAccount.setFreezePoint(userPointAccount.getFreezePoint() + freezePoints);
            int i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新用户账户");
            }

            // 4. 新增冻结记录
            UserPointFreezeRecordDO freezeRecord = userPointFreezeRecordManager.createFreezeRecord(userId, pointType,
                idempotentId, reason, freezePoints, now, UserPointFreezeRecordStatus.FREEZED);

            // 2.查询积分明细
            List<UserPointDetailDO> userPointDetailList = userPointDetailManager.queryAvailablePointRecord(userId, pointType);

            // 3.冻结积分明细
            // 全部冻结列表
            List<UserPointDetailDO> allFreezeList = new ArrayList<>();
            // K: detailCode V: points
            Map<String, Long> detailCodeMap = new HashMap<>();
            Long needFreezePoints = freezePoints;

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
                    needFreezePoints -= availablePoints;
                    allFreezeList.add(userPointDetail);
                    detailCodeMap.put(userPointDetail.getDetailCode(), availablePoints);
                }
            }

            // 3.2 冻结全部冻结列表
            if (!CollectionUtils.isEmpty(allFreezeList)) {
                userPointDetailManager.freezeDetailWithAll(allFreezeList);
            }

            // 5. 新增冻结明细记录
            for (String detailCode : detailCodeMap.keySet()) {
                UserPointFreezeRecordDetailDO userPointFreezeRecordDetail = new UserPointFreezeRecordDetailDO();
                userPointFreezeRecordDetail.setUserId(userId);
                userPointFreezeRecordDetail.setPointType(pointType);
                userPointFreezeRecordDetail.setDetailCode(detailCode);
                userPointFreezeRecordDetail.setFreezeCode(freezeRecord.getFreezeCode());
                userPointFreezeRecordDetail.setFreezePoints(detailCodeMap.get(detailCode));
                userPointFreezeRecordDetailManager.insert(userPointFreezeRecordDetail);
            }

            return BaseResult.success(freezeRecord.getFreezeCode());
        } catch (Exception e) {
            return BaseResult.fail("冻结失败");
        }
    }

    @Override
    public BaseResult<Void> unfreezePoint(String userId, String pointType, String freezeRecordCode) {
        try {
            // 查询冻结记录, 判断状态
            UserPointFreezeRecordDO freezeRecord = userPointFreezeRecordManager.queryByCode(userId, freezeRecordCode);

            if (freezeRecord == null) {
                return BaseResult.fail("冻结记录不存在");
            }
            if (UserPointFreezeRecordStatus.UNFROZE.getStatus().equals(freezeRecord.getFreezeRecordStatus())) {
                return BaseResult.fail("已解冻");
            }
            if (UserPointFreezeRecordStatus.CONSUMED.getStatus().equals(freezeRecord.getFreezeRecordStatus())) {
                return BaseResult.fail("已消耗");
            }

            // 1. 更新用户账户
            UserPointAccountDO userPointAccount = userPointAccountManager.initAndGet(userId, pointType);
            userPointAccount.setFreezePoint(userPointAccount.getFreezePoint() - freezeRecord.getFreezePoints());
            userPointAccount.setAvailablePoint(userPointAccount.getAvailablePoint() + freezeRecord.getFreezePoints());
            int i = userPointAccountManager.update(userPointAccount);
            if (i < 1) {
                throw new PointSystemException("并发更新积分账户");
            }

            // 2. 更新冻结记录
            freezeRecord.setFreezeRecordStatus(UserPointFreezeRecordStatus.UNFROZE.getStatus());
            i = userPointFreezeRecordManager.update(freezeRecord);
            if (i < 1) {
                throw new PointSystemException("并发修改冻结记录");
            }

            return BaseResult.success(null);
        } catch (Exception e) {
            return BaseResult.fail("解冻失败");
        }
    }

    @Override
    public BaseResult<Void> consumeFreezePoint(String userId, String pointType, String freezeRecordCode) {
        return null;
    }

    /**
     * 是否为有效的积分配置
     *
     * @param pointConfig 积分配置
     * @return 有效 true 无效 false
     */
    private boolean validPointConfig(PointConfigDO pointConfig) {
        return pointConfig != null;
    }

    private void mergePoint(String userId, UserPointDetailDO userPointDetail) {
        // 查询是否有当日的合并记录
        String mergeIdemId = "merge.prefix_" + userId + "_" + DateUtil.format(new Date());
        UserPointDetailDO mergeDetail = userPointDetailManager.queryByUserIdAndIdempotentId(userId, mergeIdemId);

        // 如果没有创建一条新的合并记录

        if (mergeDetail == null) {

            PointConfigDO pointConfig = pointConfigManager.getPointConfig(userPointDetail.getPointType());

            mergeDetail = new UserPointDetailDO();
            mergeDetail.setUserId(userId);
            mergeDetail.setPointType(userPointDetail.getPointType());
            mergeDetail.setDetailCode(UUID.randomUUID().toString().replaceAll("-", ""));
            mergeDetail.setIdempotentId(mergeIdemId);
            mergeDetail.setGiveReason("合并积分");
            mergeDetail.setEffectTime(userPointDetail.getEffectTime());
            // 为什么不需要计算失效时间
            mergeDetail.setExpireTime(pointConfig.calcPointExpireTime(userPointDetail.getEffectTime()));
            mergeDetail.setGivePoints(userPointDetail.getGivePoints());
            mergeDetail.setAvailablePoints(userPointDetail.getAvailablePoints());
            mergeDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
            userPointDetailManager.insert(mergeDetail);
        } else {
            int i = 0;
            // 如果有则合并进去
            if (UserPointDetailStatus.RECEIVED.getStatus().equals(mergeDetail.getDetailStatus())) {
                mergeDetail.setGivePoints(mergeDetail.getGivePoints() + userPointDetail.getGivePoints());
                mergeDetail.setAvailablePoints(mergeDetail.getAvailablePoints() + userPointDetail.getAvailablePoints());
                i = userPointDetailManager.update(mergeDetail);
            } else if (UserPointDetailStatus.FROZEN.getStatus().equals(mergeDetail.getDetailStatus())) {
                mergeDetail.setGivePoints(mergeDetail.getGivePoints() + userPointDetail.getGivePoints());
                mergeDetail.setAvailablePoints(mergeDetail.getAvailablePoints() + userPointDetail.getAvailablePoints());
                mergeDetail.setDetailStatus(UserPointDetailStatus.PART_FROZEN.getStatus());
                i = userPointDetailManager.update(mergeDetail);
            } else if (UserPointDetailStatus.PART_FROZEN.getStatus().equals(mergeDetail.getDetailStatus())) {
                mergeDetail.setGivePoints(mergeDetail.getGivePoints() + userPointDetail.getGivePoints());
                mergeDetail.setAvailablePoints(mergeDetail.getAvailablePoints() + userPointDetail.getAvailablePoints());
                i = userPointDetailManager.update(mergeDetail);
            } else if (UserPointDetailStatus.CONSUMED.getStatus().equals(mergeDetail.getDetailStatus())) {
                mergeDetail.setGivePoints(mergeDetail.getGivePoints() + userPointDetail.getGivePoints());
                mergeDetail.setAvailablePoints(mergeDetail.getAvailablePoints() + userPointDetail.getAvailablePoints());
                mergeDetail.setDetailStatus(UserPointDetailStatus.RECEIVED.getStatus());
                i = userPointDetailManager.update(mergeDetail);
            } else {
                throw new PointSystemException("不支持的合并类型");
            }

            if (i < 1) {
                throw new PointSystemException("并发冲突");
            }

        }

        // 将合并记录更新为已合并状态
        userPointDetail.setMergedPoints(userPointDetail.getAvailablePoints());
        userPointDetail.setAvailablePoints(0L);
        userPointDetail.setMergedTime(new Date());
        userPointDetail.setDetailStatus(UserPointDetailStatus.MERGED.getStatus());
        int i = userPointDetailManager.update(userPointDetail);
        if (i < 1) {
            throw new PointSystemException("并发更新");
        }

    }

}