package org.qiyu.live.common.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-14
 * @Description: 用来向Redis中安全高效的存储大list集合数据的工具类
 * @Version: 1.0
 */

public class ListUtils {


    /***
     * 将一个大list集合拆分成多个子list集合的方法
     * @param list    原始数据
     * @param subNum  子集合大小
     * @return
     * @param <T>
     */
    public static<T> List<List<T>> splitList(List<T> list,int subNum){
        List<List<T>> resultList = new ArrayList<>();
        int preIndex = 0;
        int lastIndex = 0;
        // 总共要拆分的次数
        int insertTimes=list.size()/subNum;
        List<T> subList;
        for( int i=0;i<=insertTimes;i++){
            preIndex=i*subNum;
            lastIndex=preIndex+subNum;
            if(i!=insertTimes){
                subList=list.subList(preIndex,lastIndex);
            } else {
                subList=list.subList(preIndex,list.size());
            }
            if(subList.size()>0) {
                resultList.add(subList);
            }
        }
        return resultList;
    }
}
