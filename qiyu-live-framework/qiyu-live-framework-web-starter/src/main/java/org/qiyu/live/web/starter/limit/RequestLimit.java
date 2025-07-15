package org.qiyu.live.web.starter.limit;


import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    // 我们程序允许请求的流量大小(即限流的大小)
    int limit();

    // 限流的时长
    int second();

    // 限流提示的文案
    String msg() default "请求过于频繁";

}
