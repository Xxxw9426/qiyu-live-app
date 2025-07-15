package org.qiyu.live.api.vo.resp;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品展示页面的数据响应实体类
 * @Version: 1.0
 */

public class PayProductItemVO {

    // 产品id
    private Long id;

    // 产品名字
    private String name;

    // 虚拟金币值
    private Integer coinNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCoinNum() {
        return coinNum;
    }

    public void setCoinNum(Integer coinNum) {
        this.coinNum = coinNum;
    }

    @Override
    public String toString() {
        return "PayProductVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coinNum=" + coinNum +
                '}';
    }
}
