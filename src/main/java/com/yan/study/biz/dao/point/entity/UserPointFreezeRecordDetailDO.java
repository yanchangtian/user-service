package com.yan.study.biz.dao.point.entity;

import lombok.Data;

@Data
public class UserPointFreezeRecordDetailDO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 精灵id
     */
    private String userId;
    /**
     * 积分类型
     */
    private String pointType;
    /**
     * 冻结code
     */
    private String freezeCode;
    /**
     * 详情code
     */
    private String detailCode;
    /**
     * 冻结积分数量
     */
    private Long freezePoints;
    /**
     * 版本号
     */
    private Long version;

}
