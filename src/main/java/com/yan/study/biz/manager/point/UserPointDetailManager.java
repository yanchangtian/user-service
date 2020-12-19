package com.yan.study.biz.manager.point;

import com.yan.study.biz.dao.point.entity.UserPointDetailDO;

public interface UserPointDetailManager {

    void insert(UserPointDetailDO userPointDetail);

    UserPointDetailDO queryByDetailCode(String detailCode);

    int update(UserPointDetailDO userPointDetail);

}
