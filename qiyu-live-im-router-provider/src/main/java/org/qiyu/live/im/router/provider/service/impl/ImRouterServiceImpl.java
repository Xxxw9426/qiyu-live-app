package org.qiyu.live.im.router.provider.service.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.provider.service.ImRouterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: 专门做请求转发的router层的service接口的实现类
 * @Version: 1.0
 */

@Service
public class ImRouterServiceImpl implements ImRouterService {


    @DubboReference
    private IRouterHandlerRpc iRouterHandlerRpc;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /***
     * 根据用户id找到用户所在的服务器地址
     * @param imMsgBody
     * @return
     */
    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        // 从Redis中获取到当前消息接收者所在的服务器
        String bindAddress = stringRedisTemplate.opsForValue().
                get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBody.getAppId() +":"+ imMsgBody.getUserId());
        // 如果所在地址为空
        if(StringUtils.isEmpty(bindAddress)){
            return false;
        }
        // 如果不为空的话将地址设置在rpc调用上下文中，后续Im服务接收到我们转发的消息后就可以拿取到消息接收者所在服务器的地址
        bindAddress=bindAddress.substring(bindAddress.indexOf("%"));
        RpcContext.getContext().set("ip", bindAddress);
        // 通过rpc将消息发送出去
        iRouterHandlerRpc.sendMsg(imMsgBody);
        return true;
    }


    /***
     * 批量操作：根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBodyList
     */
    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        // 拿到所有的用户id集合
        List<Long> userIdList = imMsgBodyList.stream().map(ImMsgBody::getUserId).collect(Collectors.toList());
        // 根据userId,将不同的userId的ImMsgBody对象分类存入map
        Map<Long,ImMsgBody> userIdMsgMap=imMsgBodyList.stream().collect(Collectors.toMap(ImMsgBody::getUserId, i->i));
        // 拿到appId
        Integer appId=imMsgBodyList.get(0).getAppId();
        // 从Redis中批量的获取每个用户绑定的IM服务的ip地址
        // 设置查询需要的key值
        String cacheKeyPrefix=ImCoreServerConstants.IM_BIND_IP_KEY + appId ;
        List<String> cacheKeyList=new ArrayList<>();
        userIdList.forEach(userId->{
            String cacheKey=cacheKeyPrefix +":"+ userId;
            cacheKeyList.add(cacheKey);
        });
        // 从Redis中批量获取传入集合中所有用户所在服务器的地址
        List<String> ipList=stringRedisTemplate.opsForValue().multiGet(cacheKeyList);
        // 定义一个根据Im服务器ip地址分类的用户id集合的map
        Map<String,List<Long>> userIdMap=new HashMap<>();
        ipList.forEach(ip->{
            String currentIp=ip.substring(ip.indexOf("%"));
            Long userId = Long.valueOf(ip.substring(ip.indexOf("%"), -1));
            List<Long> userIds=userIdMap.get(currentIp);
            if(userIds==null){
                userIds=new ArrayList<>();
            }
            userIds.add(userId);
            userIdMap.put(currentIp,userIds);
        });
        // 再根据map的key值(即Im服务的ip地址)对map集合进行分类进行消息的转发
        // 将连接同一台ip地址的imMsgBody组装到同一个list集合中
        for(String currentIp:userIdMap.keySet()){
            // 设置Dubbo上下文
            RpcContext.getContext().set("ip",currentIp);
            // 根据当前的ip地址获取要向这个ip地址发送信息的所有用户id集合
            List<Long> ipBindUserIdList = userIdMap.get(currentIp);
            // 设置根据不同ip地址要发送的ImMsgBody对象的集合
            List<ImMsgBody> batchSendMsgGroupByIpList=new ArrayList<>();
            for(Long userId:ipBindUserIdList){
                // 获取当前用户的发送信息的ImMsgBody对象
                ImMsgBody imMsgBody = userIdMsgMap.get(userId);
                batchSendMsgGroupByIpList.add(imMsgBody);
            }
            // 通过rpc调用将当前获取到的同一个Im服务器的消息转发给对应服务器
            iRouterHandlerRpc.batchSendMsg(batchSendMsgGroupByIpList);
        }
    }
}
