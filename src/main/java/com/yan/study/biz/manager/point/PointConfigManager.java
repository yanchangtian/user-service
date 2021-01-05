package com.yan.study.biz.manager.point;

import com.yan.study.biz.dao.point.entity.PointConfigDO;

public interface PointConfigManager {

    /**
     * 获取积分配置
     *
     * @param pointType
     * @return
     */
    PointConfigDO getPointConfig(String pointType);

}
