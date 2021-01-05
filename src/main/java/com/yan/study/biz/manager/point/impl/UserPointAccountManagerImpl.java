package com.yan.study.biz.manager.point.impl;

import com.yan.study.biz.dao.point.entity.UserPointAccountDO;
import com.yan.study.biz.manager.point.UserPointAccountManager;
import org.springframework.stereotype.Service;

@Service
public class UserPointAccountManagerImpl implements UserPointAccountManager {

    @Override
    public void insert(UserPointAccountDO userPointAccount) {

    }

    @Override
    public UserPointAccountDO query(String userId, String pointType) {
        return null;
    }

    @Override
    public UserPointAccountDO initAndGet(String userId, String pointType) {
        UserPointAccountDO userPointAccount = query(userId, pointType);
        if (userPointAccount == null) {
            // 并发问题由数据库唯一键(userId, pointType)处理, 并发创建报错
            userPointAccount = new UserPointAccountDO();
            userPointAccount.setUserId(userId);
            userPointAccount.setPointType(pointType);
            insert(userPointAccount);
        }
        return userPointAccount;
    }

    @Override
    public int update(UserPointAccountDO userPointAccount) {
        return 0;
    }

    @Override
    public void increasePoints(String userId, String pointType, Long availablePoints) {

    }

}
