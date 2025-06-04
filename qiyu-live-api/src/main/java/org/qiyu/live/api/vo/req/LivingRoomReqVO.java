package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 与前端交互的直播间相关请求的请求实体类
 * @Version: 1.0
 */

public class LivingRoomReqVO {

    private Integer type;

    private int page;

    private int pageSize;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LivingRoomReqVO{" +
                "type=" + type +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
