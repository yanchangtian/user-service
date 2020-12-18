package com.yan.study.biz.manager.point;

import com.yan.study.biz.dao.point.entity.UserPointAccountDO;
import org.springframework.stereotype.Service;

@Service
public interface UserPointAccountManager {

    void createAccount(UserPointAccountDO userPointAccount);

    UserPointAccountDO queryAccount(String userId, String pointType);

    UserPointAccountDO initAndGet(String userId, String pointType);

    Integer updateAccount(UserPointAccountDO userPointAccount);

}
