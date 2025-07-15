package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePkReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.LivingRoomRespVO;
import org.qiyu.live.api.vo.resp.RedPacketReceiveVO;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.interfaces.IRedPacketConfigRpc;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.living.dto.LivingPkRespDTO;
import org.qiyu.live.living.dto.LivingRoomReqDTO;
import org.qiyu.live.living.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.ILivingRoomRpc;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    @DubboReference
    private IRedPacketConfigRpc redPacketConfigRpc;


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
            return respVO;
        }
        // 判断当前用户是否是主播本人
        boolean isAnchor = respDTO.getAnchorId().equals(userId);
        // 如果是主播的话判断当前直播是否有配置红包雨功能
        if(isAnchor) {
            RedPacketConfigRespDTO configRespDTO = redPacketConfigRpc.queryByAnchorId(userId);
            // 当前直播配置了红包雨功能
            if(configRespDTO!=null) {
                // 给前端返回当前直播间的红包雨配置的唯一code，这样加入该直播间的用户都可以在前端拿到该唯一code
                respVO.setRedPacketConfigCode(configRespDTO.getConfigCode());
            }
        }
        // 传回当前直播间的信息(主播信息，直播信息等)
        respVO.setRoomId(respDTO.getId());
        respVO.setAnchorId(respDTO.getAnchorId());
        respVO.setAnchor(isAnchor);
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


    /***
     * 用户请求连线pk
     * @param onlinePkReqVO
     * @return
     */
    @Override
    public boolean onlinePk(OnlinePkReqVO onlinePkReqVO) {
        LivingRoomReqDTO livingRoomReqDTO = ConvertBeanUtils.convert(onlinePkReqVO, LivingRoomReqDTO.class);
        // 设置当前用户的id
        livingRoomReqDTO.setPkObjId(QiyuRequestContext.getUserId());
        livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        // 返回请求连线的结果
        LivingPkRespDTO livingPkRespDTO = livingRoomRpc.onlinePk(livingRoomReqDTO);
        return livingPkRespDTO.isOnlineStatus();
    }


    /***
     * 主播请求开始初始化红包雨红包数据
     * @param userId
     * @param roomId
     * @return
     */
    @Override
    public Boolean prepareRedPacket(Long userId, Integer roomId) {
        // 进行参数校验，判断当前请求用户是否是主播本人
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isTrue(userId.equals(livingRoomRespDTO.getAnchorId()), BizBaseErrorEnum.PARAM_ERROR);
        return redPacketConfigRpc.prepareRedPacket(userId);
    }


    /***
     * 主播请求开始抢红包活动
     *  首先判断红包数据是否已经初始化完成
     *  然后通过im将红包雨的红包数据通知给直播间的所有用户
     * @param userId
     * @param code
     * @return
     */
    @Override
    public Boolean startRedPacket(Long userId, String code) {
        // 查询当前红包活动的相关配置数据，调用rpc
        // 首先设置当前用户和红包雨红包数据配置的唯一code
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        // rpc调用根据主播id查询直播间id
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByAnchorId(userId);
        // 判断查询结果
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        reqDTO.setRoomId(livingRoomRespDTO.getId());
        // rpc调用后台抢红包功能实现方法
        return redPacketConfigRpc.startRedPacket(reqDTO);
    }


    /***
     * 用户领取红包
     * @param userId
     * @param code
     * @return
     */
    @Override
    public RedPacketReceiveVO getRedPacket(Long userId, String code) {
        // 调用rpc接口领取红包
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        RedPacketReceiveDTO receiveDTO = redPacketConfigRpc.receiveRedPacket(reqDTO);
        // 构建返回对象
        RedPacketReceiveVO respVO = new RedPacketReceiveVO();
        // 如果领取红包接口返回为空，说明领取失败
        if (receiveDTO == null) {
            respVO.setMsg("红包已派发完毕");
        // 领取成功
        } else {
            respVO.setPrice(receiveDTO.getPrice());
            respVO.setMsg(receiveDTO.getNotifyMsg());
        }
        return respVO;
    }


}
