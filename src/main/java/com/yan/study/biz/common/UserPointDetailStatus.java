package com.yan.study.biz.common;

public enum UserPointDetailStatus {

    /**
     * 可领取(预发放的积分, 还未领取)
     */
    RECEIVABLE("可领取"),
    /**
     * 已领取(已领取或直接发放的积分)
     */
    RECEIVED("已领取"),
    /**
     * 已失效(目前失效积分会直接物理删除, 所以此状态暂时无用)
     */
    INVALID("已失效"),
    /**
     * 已消耗
     */
    CONSUMED("已消耗"),
    /**
     * 已过期
     */
    EXPIRED("已过期"),
    /**
     * 部分过期 (计算过期的时候, 已经有部分积分处于冻结状态, 则会置为部分过期)
     */
    PART_EXPIRED("部分过期"),
    /**
     * 已冻结
     */
    FROZEN("已冻结"),
    /**
     * 部分冻结(需要冻结的积分小于此条明细可用的积分, 即会置为部分冻结)
     */
    PART_FROZEN("部分冻结"),
    /**
     * 已合并(意味着此条积分明细已经被合并到一条统一的详情里, 此条记录类似失效)
     */
    MERGED("已合并");

    private final String status;

    UserPointDetailStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
