package org.qiyu.live.web.starter.context;

import org.qiyu.live.web.starter.constants.RequestConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-16
 * @Description: 用户请求的上下文
 *  通过ThreadLocal来实现将我们从网关层获得到的一些重要参数写入线程本地变量
 * @Version: 1.0
 */

public class QiyuRequestContext {


    // 父子线程都可以读取
    private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<>();


    /***
     * set方法，用于将传入的key和value值设置到本地线程变量(ThreadLocal)中
     * @param key
     * @param value
     */
    public static void set(Object key, Object value) {
        // 如果key为空报错
        if(key==null) {
            throw new IllegalArgumentException("key can not be null");
        }
        // 如果value为空实质上就是移除操作，我们将当前key值对应的线程变量移除
        if(value==null) {
            resources.get().remove(key);
        }
        // 两者都不为空则取出本地线程变量将key和value写进去
        resources.get().put(key, value);
    }


    /***
     * get方法，用于根据key值从本地线程(ThreadLocal)中获取对应的线程变量
     * @param key
     * @return
     */
    public static Object get(Object key) {
        // 如果key为空报错
        if(key==null) {
            throw new IllegalArgumentException("key can not be null");
        }
        return resources.get().get(key);
    }


    /***
     * 封装好的返回本地线程变量中的userId参数的方法
     * @return
     */
    public static Long getUserId() {
        Object userId =get(RequestConstants.QIYU_USER_ID);
        return userId==null?null:(Long)userId;
    }


    /***
     *  设计一个clear方法，防止内存泄漏，因为我们的springboot-web容器处理请求的底层使用的是tomcat中的工作线程，
     *  这些工作线程会去处理我们的业务请求，但是这些线程不是处理完我们的一个业务请求就被回收的，而是会长时间存在的，
     *  这也就意味着我们同一个线程有可能会在处理完上一个业务请求后继续处理下一个新的业务请求，
     *  那么就有可能导致我们的下一个业务请求在读取线程中的参数的时候会读取到上一个业务请求残留的错误数据
     *  因此我们设置了clear方法来避免这个问题的发生
     */
    public static void clear() {
        resources.remove();
    }


    // 实现父子线程之间的线程本地变量传递，方便我们后序的异步操作
    // A-->threadLocal ("userId",1001)
    // A-->new Thread(B)-->B线程属于A线程的子线程，threadLocal get("userId")
    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {

        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap();
        }

        @Override
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            } else {
                return null;
            }
        }
    }
}
