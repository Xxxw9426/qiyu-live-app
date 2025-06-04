package org.qiyu.live.im.router.provider.cluster;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.nacos.api.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: todo：自定义的Invoker,其功能是在dubbo在做集群结点的选择的时候按照我们自定义的规则来选择
 *                 如果配置得当的话，我们的请求最终会经过这个ImRouterClusterInvoker类，我们可以在这个类中做一些过滤来获取到我们要请求的目标机器
 * @Version: 1.0
 */

public class ImRouterClusterInvoker<T> extends AbstractClusterInvoker<T> {


    public ImRouterClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List list, LoadBalance loadbalance) throws RpcException {
        checkWhetherDestroyed();
        // 从rpc的上下文中拿到我们之前设置过的请求的ip地址
        String ip= (String) RpcContext.getContext().get("ip");
        // 校验拿到的参数
        if(StringUtils.isEmpty(ip)) {
            throw new RuntimeException("ip can not be null!");
        }
        // Dubbo中会将所有的服务的提供者封装成Invoker对象来进行选择
        // 获取到所有Dubbo可以调用的Invoker对象
        // list()方法是由AbstractClusterInvoker提供的，返回当前我们要调用的rpc方法在注册中心上注册的一系列服务提供者的地址
        List<Invoker<T>> invokers=list(invocation);
        // 通过steam流来返回与我们传入ip相同的Invoker对象，实现了过滤
        Invoker<T> matchInvoker= invokers.stream().filter(invoker -> {
            // 取出我们每一个invoker的ip+port
            String serverIp=invoker.getUrl().getHost() +":"+invoker.getUrl().getPort();
            return serverIp.equals(ip);
        }).findFirst().orElse(null);
        // 如果为空说明我们传入的ip对应的用户此时应该已经不在这台机器上保持连接了，抛出异常
        if(matchInvoker == null) {
            throw new RuntimeException("ip is invalid");
        }
        // 返回找到的Invoker实现了过滤
        return matchInvoker.invoke(invocation);
    }
}
