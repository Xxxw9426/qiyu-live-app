package org.qiyu.live.living.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关正在直播的直播间相关请求的请求实体类
 * @Version: 1.0
 */

public class LivingRoomReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5514327034958547729L;

    private Integer id;

    // 开播的主播的id
    private Long anchorId;

    private Long pkObjId;

    private String roomName;

    private Integer roomId;

    private String covertImg;

    private Integer type;

    private Integer appId;

    private int page;

    private int pageSize;

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

    public Long getPkObjId() {
        return pkObjId;
    }

    public void setPkObjId(Long pkObjId) {
        this.pkObjId = pkObjId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
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
        return "LivingRoomReqDTO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", pkObjId=" + pkObjId +
                ", roomName='" + roomName + '\'' +
                ", roomId=" + roomId +
                ", covertImg='" + covertImg + '\'' +
                ", type=" + type +
                ", appId=" + appId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
