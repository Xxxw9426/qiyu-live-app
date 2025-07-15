package org.qiyu.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.qiyu.live.bank.provider.dao.mapper.IPayTopicMapper;
import org.qiyu.live.bank.provider.dao.po.PayTopicPO;
import org.qiyu.live.bank.provider.service.IPayTopicService;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付回调请求对应mq主题相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class PayTopicServiceImpl implements IPayTopicService {


    @Resource
    private IPayTopicMapper payTopicMapper;


    /***
     * 根据业务码获取支付回调请求对应mq主题相关数据实体类
     * @param code
     * @return
     */
    @Override
    public PayTopicPO getByCode(Integer code) {
        LambdaQueryWrapper<PayTopicPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayTopicPO::getBizCode,code);
        queryWrapper.last("limit 1");
        return payTopicMapper.selectOne(queryWrapper);
    }
}
