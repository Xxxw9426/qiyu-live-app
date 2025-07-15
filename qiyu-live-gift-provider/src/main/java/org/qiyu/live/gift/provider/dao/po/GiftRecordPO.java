package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务礼物记录数据库表实体类
 * @Version: 1.0
 */

@TableName("t_gift_record")
public class GiftRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 发送人
    private Long userId;

    // 接收人
    private Long objectId;

    // 礼物来源
    private Integer source;

    // 礼物价格
    private Integer price;

    // 礼物价格金额单位
    private Integer priceUnit;

    // 礼物id
    private Integer giftId;

    // 发送时间
    private Date sendTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "GiftRecordPO{" +
                "id=" + id +
                ", userId=" + userId +
                ", objectId=" + objectId +
                ", source=" + source +
                ", price=" + price +
                ", priceUnit=" + priceUnit +
                ", giftId=" + giftId +
                ", sendTime=" + sendTime +
                '}';
    }
}
