package org.qiyu.live.bank.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PayProductTypeEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.provider.dao.mapper.IPayOrderMapper;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;
import org.qiyu.live.bank.provider.dao.po.PayTopicPO;
import org.qiyu.live.bank.provider.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class PayOrderServiceImpl implements IPayOrderService {


    private static final Logger LOGGER= LoggerFactory.getLogger(PayOrderServiceImpl.class);


    @Resource
    private MQProducer mqProducer;


    @Resource
    private IPayOrderMapper payOrderMapper;


    @Resource
    private IPayTopicService payTopicService;


    @Resource
    private IPayProductService payProductService;


    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;


    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;


    /***
     * 插入订单
     * @param payOrderPO
     * @return  订单id
     */
    @Override
    public String insertOne(PayOrderPO payOrderPO) {
        String orderId = UUID.randomUUID().toString();
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insert(payOrderPO);
        return orderId;
    }


    /***
     * 根据主键id更新订单状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setId(id);
        payOrderPO.setStatus(status);
        payOrderMapper.updateById(payOrderPO);
        return true;
    }


    /***
     * 根据orderId更新订单状态
     * @param orderId
     * @param status
     * @return
     */
    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);
        LambdaUpdateWrapper<PayOrderPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PayOrderPO::getOrderId, orderId);
        payOrderMapper.update(payOrderPO,wrapper);
        return true;
    }


    /***
     * 支付回调请求处理逻辑
     * @param payOrderDTO
     * @return
     */
    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        // 发送mq根据不同的业务场景下的支付回调消息调用不同的业务服务进行后续处理
        // 获取业务码
        Integer bizCode = payOrderDTO.getBizCode();
        // 根据业务码获取当前mq的主题
        PayTopicPO topicPO = payTopicService.getByCode(bizCode);
        // 参数校验
        if(topicPO == null || StringUtils.isEmpty(topicPO.getTopic())){
            LOGGER.error("error topicPO, payOrderDTO is {}",payOrderDTO);
            return false;
        }
        // 获取当前订单的详细信息
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        // 参数校验
        if(payOrderPO == null){
            LOGGER.error("error payOrderPO, payOrderDTO is {}",payOrderDTO);
            return false;
        }
        /***
         * 对数据库的写操作封装的方法：
         *   增长用户余额
         *   插入交易记录
         *   更新用户订单状态
         */
        this.payNotifyHandler(payOrderPO);
        // 设置MQ中要发送的消息
        Message message=new Message();
        message.setTopic(topicPO.getTopic());
        message.setBody(JSON.toJSONBytes(payOrderPO));
        try {
            // 给获取到的特定的topic发送mq消息
            SendResult sendResult = mqProducer.send(message);
        } catch (Exception e) {
            LOGGER.error("[payNotify] error is",e);
        }
        return true;
    }


    /***
     * 对数据库的写操作封装的方法：
     *   增长用户余额
     *   插入交易记录
     *   更新用户订单状态
     * @param payOrderPO
     */
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        // 更新用户订单状态为已支付
        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
        // 首先根据订单中的产品获取产品的虚拟金额
        Integer productId = payOrderPO.getProductId();
        PayProductDTO payProductDTO = payProductService.getByProductId(productId);
        // 如果当前场景是直播场景并且参数校验通过
        if(payProductDTO != null && PayProductTypeEnum.QIYU_COIN.getCode().equals(payProductDTO.getType())){
            // 将拓展字段转化为JSONObject对象
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            Long userId=payOrderPO.getUserId();
            Integer num= jsonObject.getInteger("coin");
            // 获取到拓展字段中当前用户要增长的虚拟金币值并且调用用户账户服务中的增长用户余额的方法
            // 增加用户的余额并且添加一条流水记录
            qiyuCurrencyAccountService.incr(userId,num);
        }
    }


    /***
     * 根据orderId查询当前订单的详细信息
     * @param orderId
     * @return
     */
    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayOrderPO::getOrderId, orderId);
        wrapper.last("limit 1");
        return payOrderMapper.selectOne(wrapper);
    }
}
