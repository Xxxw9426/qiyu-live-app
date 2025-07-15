package org.qiyu.live.api.vo;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 向前端返回初始化直播页面时需要的数据的实体类
 * @Version: 1.0
 */

public class LivingRoomInitVO {

    private Long anchorId;

    private Long userId;

    private String nickName;

    private String anchorImg;

    private String roomName;

    private boolean isAnchor;

    // 直播间红包雨的配置数据的唯一code
    private String redPacketConfigCode;

    private String avatar;

    private Integer roomId;

    private String watcherNickName;

    private String anchorNickName;

    // 观众头像
    private String watcherAvatar;

    // 默认背景图，为了方便讲解使用
    private String defaultBgImg;

    private Long pkObjId;

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAnchorImg() {
        return anchorImg;
    }

    public void setAnchorImg(String anchorImg) {
        this.anchorImg = anchorImg;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isAnchor() {
        return isAnchor;
    }

    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
    }

    public String getRedPacketConfigCode() {
        return redPacketConfigCode;
    }

    public void setRedPacketConfigCode(String redPacketConfigCode) {
        this.redPacketConfigCode = redPacketConfigCode;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getWatcherNickName() {
        return watcherNickName;
    }

    public void setWatcherNickName(String watcherNickName) {
        this.watcherNickName = watcherNickName;
    }

    public String getAnchorNickName() {
        return anchorNickName;
    }

    public void setAnchorNickName(String anchorNickName) {
        this.anchorNickName = anchorNickName;
    }

    public String getWatcherAvatar() {
        return watcherAvatar;
    }

    public void setWatcherAvatar(String watcherAvatar) {
        this.watcherAvatar = watcherAvatar;
    }

    public String getDefaultBgImg() {
        return defaultBgImg;
    }

    public void setDefaultBgImg(String defaultBgImg) {
        this.defaultBgImg = defaultBgImg;
    }

    public Long getPkObjId() {
        return pkObjId;
    }

    public void setPkObjId(Long pkObjId) {
        this.pkObjId = pkObjId;
    }

    @Override
    public String toString() {
        return "LivingRoomInitVO{" +
                "anchorId=" + anchorId +
                ", userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", anchorImg='" + anchorImg + '\'' +
                ", roomName='" + roomName + '\'' +
                ", isAnchor=" + isAnchor +
                ", redPacketConfigCode='" + redPacketConfigCode + '\'' +
                ", avatar='" + avatar + '\'' +
                ", roomId=" + roomId +
                ", watcherNickName='" + watcherNickName + '\'' +
                ", anchorNickName='" + anchorNickName + '\'' +
                ", watcherAvatar='" + watcherAvatar + '\'' +
                ", defaultBgImg='" + defaultBgImg + '\'' +
                ", pkObjId=" + pkObjId +
                '}';
    }
}
