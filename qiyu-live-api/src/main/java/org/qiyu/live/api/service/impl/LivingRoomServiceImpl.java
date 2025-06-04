package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.LivingRoomRespVO;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static org.yaml.snakeyaml.nodes.NodeId.anchor;

/**
*@Author: 萱子王
*@CreateTime: 2025-06-02
*@Description: 有关正在直播的直播间的相关操作的service接口实现类
*@Version: 1.0
*/
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {


    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    @DubboReference
    private IUserRpc userRpc;


    /***
     * 开播的逻辑操作
     * @param type
     * @return
     */
    @Override
    public Integer startingLiving(Integer type) {
        Long userId=QiyuRequestContext.getUserId();
        // 首先查询当前要开播的用户的个人基础信息
        UserDTO userDTO = userRpc.getUserById(userId);
        // 封装要开播的用户的实体类对象
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        // 从上下文设置开播用户id
        livingRoomReqDTO.setAnchorId(userId);
        // 设置直播间封面(使用用户的头像)
        livingRoomReqDTO.setCovertImg(userDTO.getAvatar());
        // 设置直播间的名字
        livingRoomReqDTO.setRoomName("主播-"+userId+"的直播间");
        // 设置直播种类
        livingRoomReqDTO.setType(type);
        // 传入业务下游的开播逻辑处理
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }


    /***
     * 关播的逻辑操作
     * @param roomId
     * @return
     */
    @Override
    public boolean closeLiving(Integer roomId) {
        // 封装要关闭直播间的相关数据的实体类
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        return livingRoomRpc.closeLiving(livingRoomReqDTO);
    }


    /***
     * 根据直播间id获取直播相关配置信息
     * @param roomId
     * @return
     */
    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        // 根据直播间id查询直播间相关信息
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        UserDTO userDTO=userRpc.getUserById(userId);
        // 响应类
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        // 传回当前进入直播间的用户(即发起获取当前直播间信息的用户的个人信息)
        respVO.setNickName(userDTO.getNickName());
        respVO.setUserId(userId);
        respVO.setAvatar(StringUtils.isEmpty(userDTO.getAvatar()) ? "https://s1.ax1x.com/2022/12/18/zb6q6f.png" :userDTO.getAvatar());
        if(respDTO == null || respDTO.getAnchorId()==null || userId==null) {
            respVO.setRoomId(-1);
        } else {
            // 传回当前直播间的信息(主播信息，直播信息等)
            respVO.setRoomId(respDTO.getId());
            respVO.setAnchorId(respDTO.getAnchorId());
            respVO.setAnchor(respDTO.getAnchorId().equals(userId));
        }
        return respVO;
    }


    /***
     * 直播间列表加载展示
     * @param livingRoomReqVO
     * @return
     */
    @Override
    public LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO) {
        LivingRoomReqDTO reqDTO = ConvertBeanUtils.convert(livingRoomReqVO, LivingRoomReqDTO.class);
        PageWrapper<LivingRoomRespDTO> resultList = livingRoomRpc.list(reqDTO);
        LivingRoomPageRespVO livingRoomPageRespVO=new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultList.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHasNext(resultList.isHasNext());
        return livingRoomPageRespVO;
    }
}
