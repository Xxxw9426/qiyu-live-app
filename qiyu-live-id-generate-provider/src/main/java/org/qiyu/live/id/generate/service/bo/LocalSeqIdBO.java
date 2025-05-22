package org.qiyu.live.id.generate.service.bo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: 本地缓存中的有序id存储对象
 * @Version: 1.0
 */

public class LocalSeqIdBO {

    private int id;

    /***
     * 内存中记录的当前有序id的值
     */
    private AtomicLong currentNum;

    /***
     * 当前id段的起始值
     */
    private Long currentStart;

    /***
     * 当前id段的结束值
     */
    private Long nextThreshold;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AtomicLong getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(AtomicLong currentNum) {
        this.currentNum = currentNum;
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
