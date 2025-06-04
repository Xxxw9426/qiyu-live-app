package org.qiyu.live.api.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.ImService;
import org.qiyu.live.api.vo.resp.ImConfigVO;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 前端接入IM服务器相关请求的service接口实现类
 * @Version: 1.0
 */

@Service
public class ImServiceImpl implements ImService {


    @Resource
    private ImTokenRpc imTokenRpc;


    // 通过使用springCloud提供的DiscoveryClient来从配置中心读取配置
    @Resource
    private DiscoveryClient discoveryClient;


    /***
     * 返回前端接入IM服务器需要的用户认证token和IM服务器地址
     * @return
     */
    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO=new ImConfigVO();
        // 通过rpc调用生成token
        imConfigVO.setToken(imTokenRpc.createImLoginToken(QiyuRequestContext.getUserId(), AppIdEnum.QIYU_LIVE_BIZ.getCode()));
        // 通过从Nacos中拉取IM服务的地址
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }


    /***
     * 从Nacos中读取配置文件获取用户认证token和IM服务器地址
     * @param imConfigVO
     */
    private void buildImServerAddress(ImConfigVO imConfigVO) {
        List<ServiceInstance> instances = discoveryClient.getInstances("qiyu-live-im-core-server");
        Collections.shuffle(instances);
        ServiceInstance aimInstance = instances.get(0);
        imConfigVO.setWsImServerAddress(aimInstance.getHost()+":8085");
        imConfigVO.setTcpImServerAddress(aimInstance.getHost()+":9090");
    }
}
