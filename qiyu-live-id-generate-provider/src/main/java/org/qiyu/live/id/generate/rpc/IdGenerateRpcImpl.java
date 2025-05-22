package org.qiyu.live.id.generate.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.id.generate.interfaces.IdGenerateRpc;
import org.qiyu.live.id.generate.service.IdGenerateService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: 分布式Id生成器Rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {


    @Resource
    private IdGenerateService idGenerateService;


   /***
     * 获取有序的分布式Id的Rpc接口
     * @param id
     * @return
     */
    @Override
    public Long getSeqId(Integer id) {
        return idGenerateService.getSeqId(id);
    }


    /***
     * 获取无序的分布式Id的Rpc接口
     * @param id
     * @return
     */
    @Override
    public Long getUnSeqId(Integer id) {
        return idGenerateService.getUnSeqId(id);
    }
}
