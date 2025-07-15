package org.qiyu.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.provider.dao.mapper.IPayProductMapper;
import org.qiyu.live.bank.provider.dao.po.PayProductPO;
import org.qiyu.live.bank.provider.service.IPayProductService;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class PayProductServiceImpl implements IPayProductService {


    @Resource
    private IPayProductMapper payProductMapper;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;


    /***
     * 返回传入的type类型的付费产品实体类对象集合
     * @param type
     * @return
     */
    @Override
    public List<PayProductDTO> products(Integer type) {
        // 首先从Redis缓存中查询当前type的付费产品
        String cacheKey = bankProviderCacheKeyBuilder.buildPayProductCache(type);
        // 每次拿取30个
        List<PayProductDTO> cacheList = redisTemplate.opsForList().range(cacheKey, 0, 30).stream().map(x->{return (PayProductDTO)x;}).collect(Collectors.toList());
        // 如果缓存命中
        if(!CollectionUtils.isEmpty(cacheList)) {
            // 如果是空值缓存
            if(cacheList.get(0).getId()==null) {
                return Collections.emptyList();
            }
            return cacheList;
        }
        // 缓存未命中，从数据库中进行查询
        LambdaQueryWrapper<PayProductPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayProductPO::getType, type);
        wrapper.eq(PayProductPO::getValidStatus, CommonStatusEnum.VALID_STATUS.getCode());
        wrapper.orderByDesc(PayProductPO::getPrice);
        List<PayProductDTO> payProductDTOS = ConvertBeanUtils.convertList(payProductMapper.selectList(wrapper), PayProductDTO.class);
        // 如果从数据库中查询到的结果为空
        if(CollectionUtils.isEmpty(payProductDTOS)) {
            // 防止缓存击穿，设置空值缓存
            redisTemplate.opsForList().leftPush(cacheKey,new PayProductDTO());
            redisTemplate.expire(cacheKey,3, TimeUnit.MINUTES);
            // 返回空
            return Collections.emptyList();
        }
        // 从数据库中查询到了数据
        // 写入redis缓存
        redisTemplate.opsForList().leftPushAll(cacheKey,payProductDTOS.toArray());
        redisTemplate.expire(cacheKey,30, TimeUnit.MINUTES);
        return payProductDTOS;
    }


    /***
     * 根据付费产品id查询付费产品实体类对象
     * @param productId
     * @return
     */
    @Override
    public PayProductDTO getByProductId(Integer productId) {
        // 复用Redis的List，但是要传递type值并且需要遍历
        // 不进行复用，重新在Redis中存储
        // 考虑到接口的友好性，我们选择不传入type,多存一个Redis对象
        // 首先从Redis中进行查询
        String cacheKey = bankProviderCacheKeyBuilder.buildPayProductItemCache(productId);
        PayProductDTO payProductDTO= (PayProductDTO) redisTemplate.opsForValue().get(cacheKey);
        // 如果从Redis中查询命中
        if(payProductDTO!=null) {
            // Redis中命中的不是空值缓存
            if(payProductDTO.getId()!=null) {
                return payProductDTO;
            }
            return null;
        }
        // Redis未命中，从数据库进行查询
        PayProductPO payProductPO = payProductMapper.selectById(productId);
        // 数据库查询到了数据
        if(payProductPO!=null) {
            PayProductDTO productDTO = ConvertBeanUtils.convert(payProductPO, PayProductDTO.class);
            redisTemplate.opsForValue().set(cacheKey,productDTO,30,TimeUnit.MINUTES);
            return productDTO;
        }
        // 数据库未查询到数据，设置空值缓存
        redisTemplate.opsForValue().set(cacheKey,new PayProductDTO(),5,TimeUnit.MINUTES);
        return null;
    }
}
