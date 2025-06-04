package org.qiyu.live.api.vo.resp;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 与前端交互的直播间分页查询相关请求的响应实体类
 * @Version: 1.0
 */

public class LivingRoomPageRespVO {

    private List<LivingRoomRespVO> list;

    private boolean hasNext;

    public List<LivingRoomRespVO> getList() {
        return list;
    }

    public void setList(List<LivingRoomRespVO> list) {
        this.list = list;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return "LivingRoomPageRespVO{" +
                "list=" + list +
                ", hasNext=" + hasNext +
                '}';
    }
}
