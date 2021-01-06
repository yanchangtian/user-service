package com.yan.study.biz.manager.point;

import com.yan.study.biz.common.UserPointFreezeRecordStatus;
import com.yan.study.biz.dao.point.entity.UserPointFreezeRecordDO;

import java.util.Date;

public interface UserPointFreezeRecordManager {

    void insert(UserPointFreezeRecordDO userPointFreezeRecord);

    /**
     *
     *
     * @param userId
     * @param pointType
     * @param idempotentId
     * @param reason
     * @param freezePoints
     * @param freezeTime
     * @param status
     * @return
     */
    UserPointFreezeRecordDO createFreezeRecord(String userId, String pointType, String idempotentId, String reason,
                                               Long freezePoints, Date freezeTime, UserPointFreezeRecordStatus status);

    /**
     * 查询通过冻结记录code
     *
     * @param userId 精灵id 分表键
     * @param freezeRecordCode 冻结记录code
     * @return 积分冻结记录
     */
    UserPointFreezeRecordDO queryByCode(String userId, String freezeRecordCode);

    /**
     * 更新积分冻结记录
     *
     * @param userPointFreezeRecord
     * @return
     */
    int update(UserPointFreezeRecordDO userPointFreezeRecord);

}
