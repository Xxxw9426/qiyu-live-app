package org.qiyu.live.id.generate.interfaces;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: 分布式Id生成器Rpc接口
 * @Version: 1.0
 */

public interface IdGenerateRpc {

    /***
     * 获取有序的分布式Id的Rpc接口
     * @param id
     * @return
     */
    Long getSeqId(Integer id);


    /***
     * 获取无序的分布式Id的Rpc接口
     * @param id
     * @return
     */
    Long getUnSeqId(Integer id);
}
