package org.qiyu.live.im.core.server.starter;

import io.micrometer.common.util.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImMsgDecoder;
import org.qiyu.live.im.core.server.common.ImMsgEncoder;
import org.qiyu.live.im.core.server.handler.tcp.TcpImServerCoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-22
 * @Description: netty启动类
 * @Version: 1.0
 */

@Configuration
@RefreshScope
public class NettyImServerStarter implements InitializingBean {


    private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerStarter.class);


    @Resource
    private TcpImServerCoreHandler imServerCoreHandler;


    @Resource
    private Environment environment;


    // 指定监听的端口
    @Value("${qiyu.im.port}")
    private int port;


    /***
     *  基于netty启动一个java进程，绑定监听的端口
     * @throws InterruptedException
     */
    public void startApplication() throws InterruptedException {
        // 接下来就是netty的常见常用代码模板
        // 首先定义bossGroup和workerGroup
        // bossGroup主要用来处理accept(连接)之类的事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // workerGroup主要用来处理read(读取数据)和write(写回数据)之类的事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        // netty初始化相关的handler
        // 在bootstrap中设置连接之后的初始化操作
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                // 打印日志，方便观察
                LOGGER.info("初始化连接渠道");
                // 设计消息体
                // 增加编解码器
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                // 设置netty的处理handler
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
        // 基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        try {
            // String registryIp = InetAddress.getLocalHost().getHostAddress();
            // 部署时我们使用这条语句获取ip地址
            String registryIp = environment.getProperty("DUBBO_IP_TO_REGISTRY");
            String registryPort = environment.getProperty("DUBBO_PORT_TO_REGISTRY");
            System.out.println(registryIp + ":" + registryPort);
            if(StringUtils.isEmpty(registryIp) || StringUtils.isEmpty(registryPort)) {
                throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
            }
            ChannelHandlerContextCache.setServerIpAddress(registryIp + ":" + registryPort);
            LOGGER.info("Netty服务启动成功，机器启动ip和dubbo服务端口为{}", registryIp + ":" + registryPort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 绑定端口并且设置同步
        ChannelFuture sync = bootstrap.bind(this.port).sync();
        LOGGER.info("服务启动成功，监听端口为：" + this.port);
        // 对ChannelFuture对象进行监听
        // 这个方法会同步阻塞，使服务长期开启
        sync.channel().closeFuture().sync();
    }


    /***
     *  这个方法的作用是在初始化spring容器后被调用启动netty服务
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startApplication();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "qiyu-live-im-server").start();
    }

}

