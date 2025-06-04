package org.qiyu.live.im.router.provider.cluster;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: todo:基于Dubbo提供的Cluster的SPI扩展类，实现根据rpc上下文中的ip参数来选择具体要请求的机器
 * @Version: 1.0
 */

public class ImRouterCluster implements Cluster {

    @Override
    public <T> Invoker<T> join(Directory<T> directory, boolean buildFilterChain) throws RpcException {
        return new ImRouterClusterInvoker<>(directory);
    }
}
