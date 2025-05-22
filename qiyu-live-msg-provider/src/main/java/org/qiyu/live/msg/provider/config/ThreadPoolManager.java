package org.qiyu.live.msg.provider.config;

import java.util.concurrent.*;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 线程池管理类
 * @Version: 1.0
 */

public class ThreadPoolManager {

    public static ThreadPoolExecutor commonAsyncPool = new ThreadPoolExecutor(2, 8, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000)
            , new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread newThread = new Thread(r);
            newThread.setName(" commonAsyncPool - " + ThreadLocalRandom.current().nextInt(10000));
            return newThread;
        }
    });
}
