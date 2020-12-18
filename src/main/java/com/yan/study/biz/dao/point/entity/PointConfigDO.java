package com.yan.study.biz.dao.point.entity;

import lombok.Data;

@Data
public class PointConfigDO {

    private Long id;

    private String pointType;

    private String pointName;

    private String pointDesc;

    /**
     * 积分预发放失效类型: 一日 两日 三日
     */
    private String preGiveInvalidType;

    /**
     * 积分过期类型
     */
    private String expireType;

}
