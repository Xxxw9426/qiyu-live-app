package org.qiyu.live.api.vo.resp;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 前端接入IM服务器请求的响应实体类
 *  返回当前用户的token和IM服务器的地址
 * @Version: 1.0
 */

public class ImConfigVO {

    // 当前用户的token
    private String token;

    // IM-WebSocket服务器的地址
    private String wsImServerAddress;

    // IM-TCP服务器的地址
    private String tcpImServerAddress;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWsImServerAddress() {
        return wsImServerAddress;
    }

    public void setWsImServerAddress(String wsImServerAddress) {
        this.wsImServerAddress = wsImServerAddress;
    }

    public String getTcpImServerAddress() {
        return tcpImServerAddress;
    }

    public void setTcpImServerAddress(String tcpImServerAddress) {
        this.tcpImServerAddress = tcpImServerAddress;
    }

    @Override
    public String toString() {
        return "ImConfigVO{" +
                "token='" + token + '\'' +
                ", wsImServerAddress='" + wsImServerAddress + '\'' +
                ", tcpImServerAddress='" + tcpImServerAddress + '\'' +
                '}';
    }
}
