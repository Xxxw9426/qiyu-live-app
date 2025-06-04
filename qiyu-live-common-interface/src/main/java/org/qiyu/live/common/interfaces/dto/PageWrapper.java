package org.qiyu.live.common.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关分页查询相关操作数据的包装类
 * @Version: 1.0
 */

public class PageWrapper<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6304531648603181862L;

    private List<T> list;

    private boolean hasNext;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
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
        return "PageWrapper{" +
                "list=" + list +
                ", hasNext=" + hasNext +
                '}';
    }
}
