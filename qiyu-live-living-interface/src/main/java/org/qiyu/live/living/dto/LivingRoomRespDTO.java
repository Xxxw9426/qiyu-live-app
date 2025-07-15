package org.qiyu.live.living.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关正在直播的直播间的相关请求的响应实体类
 * @Version: 1.0
 */

public class LivingRoomRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1726966822697932058L;

    private Integer id;

    private Long anchorId;

    private String roomName;

    private String covertImg;

    private Integer type;

    private Integer watchNum;

    private Integer goodNum;

    private Long pkObjId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Long getPkObjId() {
        return pkObjId;
    }

    public void setPkObjId(Long pkObjId) {
        this.pkObjId = pkObjId;
    }


    @Override
    public String toString() {
        return "LivingRoomRespDTO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", roomName='" + roomName + '\'' +
                ", covertImg='" + covertImg + '\'' +
                ", type=" + type +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", pkObjId=" + pkObjId +
                '}';
    }
}
