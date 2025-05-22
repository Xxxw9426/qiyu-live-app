package org.qiyu.live.user.utils;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-06
 * @Description: 用户标签工具类
 * @Version: 1.0
 */

public class TagInfoUtils {

    /***
     * 判断数据库中的传入matchTag中是否包含tagInfo
     * @param tagInfo  用户当前的标签值
     * @param matchTag  被查询是否匹配的标签值
     * @return
     */
    public static boolean isContain(Long tagInfo,Long matchTag) {
        return tagInfo!=null && matchTag !=null && matchTag > 0 && (tagInfo & matchTag) == matchTag;
    }
}
