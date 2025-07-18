package bio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-23
 * @Description: bio客户端
 * @Version: 1.0
 */

public class BioClient {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger connectCount = new AtomicInteger(0);
        //连接bio server
        CountDownLatch count = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket();
                        count.await();
                        socket.connect(new InetSocketAddress(9090));
                        System.out.println("连接完成" + connectCount.getAndIncrement());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
        count.countDown();
        Thread.sleep(100000);
    }
}