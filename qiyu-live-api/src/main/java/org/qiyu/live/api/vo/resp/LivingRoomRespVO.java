package org.qiyu.live.api.vo.resp;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 与前端交互的直播间相关请求的响应实体类
 * @Version: 1.0
 */

public class LivingRoomRespVO {

    // 直播间id
    private Integer id;

    // 直播间名称
    private String roomName;

    // 主播id
    private Long anchorId;

    // 观看数目
    private Integer watchNum;

    // 点赞数目
    private Integer goodNum;

    // 直播间封面
    private String convertImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
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

    public String getConvertImg() {
        return convertImg;
    }

    public void setConvertImg(String convertImg) {
        this.convertImg = convertImg;
    }

    @Override
    public String toString() {
        return "LivingRoomRespVO{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", anchorId=" + anchorId +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", convertImg='" + convertImg + '\'' +
                '}';
    }
}
