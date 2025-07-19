package org.qiyu.live.gift.provider.rpc;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.*;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.IShopCarService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class SkuOrderInfoRpcImpl implements ISkuOrderInfoRpc {


    @Resource
    private MQProducer mqProducer;


    @Resource
    private IShopCarService shopCarService;


    @Resource
    private ISkuInfoService skuInfoService;


    @Resource
    private ISkuStockInfoService skuStockInfoService;


    @Resource
    private ISkuOrderInfoService skuOrderInfoService;


    @DubboReference
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;


    /***
     * 根据用户id和直播间id查询当前用户在当前直播间的商品订单
     * @param userId
     * @param roomId
     * @return
     */
    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId, roomId);
    }


    /***
     * 插入一条商品订单信息
     * @param reqDTO
     * @return
     */
    @Override
    public boolean insertOne(SkuOrderInfoReqDTO reqDTO) {
        return skuOrderInfoService.insertOne(reqDTO)!=null;
    }


    /***
     * 根据商品订单的id修改商品订单的状态
     * @param reqDTO
     * @return
     */
    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO) {
        return skuOrderInfoService.updateOrderStatus(reqDTO);
    }


    /***
     * 生成一条预支付订单
     * @param reqDTO
     * @return
     */
    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO reqDTO) {
        // 为了防止重复请求，我们首先判断当前用户的购物车是否已经被清空
        // 拿到购物车对象
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqDTO, ShopCarReqDTO.class);
        ShopCarRespDTO carInfo = shopCarService.getCarInfo(shopCarReqDTO);
        // 获取到购物车对象中的所有商品信息列表
        List<ShopCarItemRespDTO> carItemList = carInfo.getSkuCarItemRespDTOS();
        // 如果购物车为空，说明用户已经清空过了购物车，不进行操作
        if (CollectionUtils.isEmpty(carItemList)) {
            return null;
        }
        // 购物车不为空
        // 拿到所有商品的skuId的集合
        List<Long> skuIdList = carItemList.stream().map(item -> item.getSkuInfoDTO().getSkuId()).collect(Collectors.toList());
        // 扣减库存，锁定库存
        boolean status=skuStockInfoService.decrStockNumBySkuIdV3(skuIdList, 1);
        // 扣减库存成功后执行插入待支付订单操作
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setSkuIdList(skuIdList);
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.PREPARE_PAY.getCode());
        skuOrderInfoReqDTO.setRoomId(reqDTO.getRoomId());
        skuOrderInfoReqDTO.setUserId(reqDTO.getUserId());
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);

        // 生成待支付订单后发送库存回滚的延迟MQ消息
        stockRollBackHandler(reqDTO.getUserId(), skuOrderInfoPO.getId());

        // 清空购物车
        // shopCarService.removeFromCar(shopCarReqDTO);
        // 设置返回结果类型
        // 首先拿出购物车中的商品信息集合
        List<ShopCarItemRespDTO> shopCarItemRespDTOS = carInfo.getSkuCarItemRespDTOS();
        List<SkuPrepareOrderItemInfoDTO> itemList = new ArrayList<>();
        Integer totalPrice = 0;
        // 遍历构建结果对象中的信息
        for (ShopCarItemRespDTO shopCarItemRespDTO : shopCarItemRespDTOS) {
            SkuPrepareOrderItemInfoDTO orderItemInfoDTO = new SkuPrepareOrderItemInfoDTO();
            orderItemInfoDTO.setSkuInfoDTO(shopCarItemRespDTO.getSkuInfoDTO());
            orderItemInfoDTO.setCount(shopCarItemRespDTO.getCount());
            itemList.add(orderItemInfoDTO);
            totalPrice = totalPrice + shopCarItemRespDTO.getSkuInfoDTO().getSkuPrice();
        }
        SkuPrepareOrderInfoDTO skuPrepareOrderInfoDTO = new SkuPrepareOrderInfoDTO();
        skuPrepareOrderInfoDTO.setSkuPrepareOrderItemInfoDTOS(itemList);
        skuPrepareOrderInfoDTO.setTotalPrice(totalPrice);
        return skuPrepareOrderInfoDTO;
    }


    /***
     * 库存回滚处理器
     * @param userId
     * @param orderId
     */
    private void stockRollBackHandler(Long userId,Long orderId) {
        RollBackStockDTO rollbackStockDTO = new RollBackStockDTO();
        rollbackStockDTO.setUserId(userId);
        rollbackStockDTO.setOrderId(orderId);
        Message message=new Message();
        message.setTopic(GiftProviderTopicNames.ROLL_BACK_STOCK);
        message.setBody(JSON.toJSONBytes(rollbackStockDTO));
        message.setDelayTimeLevel(16); // 30min的延迟时间
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /***
     * 用户点击立即支付后后端根据订单信息扣减余额
     * @param payNowReqDTO
     * @return
     */
    @Override
    public boolean payNow(PayNowReqDTO payNowReqDTO) {
        // 首先查询该订单信息
        SkuOrderInfoRespDTO skuOrderInfo = skuOrderInfoService.queryByUserIdAndRoomId(payNowReqDTO.getUserId(), payNowReqDTO.getRoomId());
        // 判断是否是待支付状态
        // 如果不是待支付状态
        if (!skuOrderInfo.getStatus().equals(SkuOrderInfoEnum.PREPARE_PAY.getCode())) {
            return false;
        }
        // 如果是的话
        // 获取到订单中的skuIdList集合
        List<Long> skuIdList = Arrays.stream(skuOrderInfo.getSkuIdList().split(",")).map(Long::valueOf).collect(Collectors.toList());
        // 根据skuId的集合查询所有商品信息的集合
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        // 拿出所有商品的价格计算商品的总价
        Integer totalPrice = skuInfoPOS.stream().map(SkuInfoPO::getSkuPrice).reduce(Integer::sum).orElse(0);
        // 获取余额并判断余额是否充足
        Integer balance = qiyuCurrencyAccountRpc.getBalance(payNowReqDTO.getUserId());
        // 余额不足
        if (balance < totalPrice) {
            return false;
        }
        // 余额充足
        // 余额扣减
        qiyuCurrencyAccountRpc.decr(payNowReqDTO.getUserId(), totalPrice);
        // 更改订单状态为已支付
        SkuOrderInfoReqDTO reqDTO = ConvertBeanUtils.convert(skuOrderInfo, SkuOrderInfoReqDTO.class);
        reqDTO.setStatus(SkuOrderInfoEnum.HAS_PAY.getCode());
        skuOrderInfoService.updateOrderStatus(reqDTO);
        return true;
    }
}
