package org.qiyu.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 正在直播的直播间数据库表实体类
 * @Version: 1.0
 */

@TableName("t_living_room")
public class LivingRoomPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 开播的主播的id
    private Long anchorId;

    private Integer type;

    private String roomName;

    private String covertImg;

    private Integer status;

    private Integer watchNum;

    private Integer goodNum;

    private Date startTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCovertImg() {
        return covertImg;
    }

    public void setCovertImg(String covertImg) {
        this.covertImg = covertImg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(Integer watchNum) {
        this.watchNum = watchNum;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "LivingRoomPO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", type=" + type +
                ", roomName='" + roomName + '\'' +
                ", covertImg='" + covertImg + '\'' +
                ", status=" + status +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", startTime=" + startTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
