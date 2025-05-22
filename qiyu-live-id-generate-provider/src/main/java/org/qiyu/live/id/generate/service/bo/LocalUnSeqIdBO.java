package org.qiyu.live.id.generate.service.bo;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-03
 * @Description: 本地缓存中的无序id存储对象
 * @Version: 1.0
 */

public class LocalUnSeqIdBO {

    private int id;

    /**
     * 提前将无序的id存放在这条队列中，需要的时候每次拿一个
     */
    private ConcurrentLinkedQueue<Long> idQueue;

    /**
     * 当前id段的开始值
     */
    private Long currentStart;

    /**
     * 当前id段的结束值
     */
    private Long nextThreshold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ConcurrentLinkedQueue<Long> getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(ConcurrentLinkedQueue<Long> idQueue) {
        this.idQueue = idQueue;
    }

    public Long getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(Long currentStart) {
        this.currentStart = currentStart;
    }

    public Long getNextThreshold() {
        return nextThreshold;
    }

    public void setNextThreshold(Long nextThreshold) {
        this.nextThreshold = nextThreshold;
    }
}
