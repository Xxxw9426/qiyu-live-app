package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.provider.dao.mapper.GiftConfigMapper;
import org.qiyu.live.gift.provider.dao.po.GiftConfigPO;
import org.qiyu.live.gift.provider.service.IGiftConfigService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务礼物配置service接口实现类
 * @Version: 1.0
 */
@Service
public class GiftConfigServiceImpl implements IGiftConfigService {


    @Resource
    private GiftConfigMapper giftConfigMapper;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /***
     * 根据礼物id查询礼物的配置信息
     * @param giftId
     * @return
     */
    @Override
    public GiftConfigDTO getByGiftId(Integer giftId) {
        LambdaQueryWrapper<GiftConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GiftConfigPO::getGiftId, giftId);
        queryWrapper.eq(GiftConfigPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        GiftConfigPO giftConfigPO = giftConfigMapper.selectOne(queryWrapper);
        return ConvertBeanUtils.convert(giftConfigPO, GiftConfigDTO.class);
    }


    /***
     * 查询所有礼物信息
     * @return
     */
    @Override
    public List<GiftConfigDTO> queryGiftList() {
        // 首先从Redis中进行查询
        String cacheKey = cacheKeyBuilder.buildGiftListCacheKey();
        List<GiftConfigDTO> giftConfigDTOS = redisTemplate.opsForList().range(cacheKey, 0, -1).stream().map(x -> (GiftConfigDTO) x).collect(Collectors.toList());
        // 如果缓存命中
        if (!CollectionUtils.isEmpty(giftConfigDTOS)) {
            // 如果是空值缓存返回空
            if (giftConfigDTOS.get(0).getGiftId() == null) {
                return Collections.emptyList();
            }
            // 不是空值缓存返回我们查询到的结果
            return giftConfigDTOS;
        }
        // 缓存未命中，从数据库进行查询
        LambdaQueryWrapper<GiftConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GiftConfigPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        List<GiftConfigPO> giftConfigPOList = giftConfigMapper.selectList(queryWrapper);
        giftConfigDTOS = ConvertBeanUtils.convertList(giftConfigPOList, GiftConfigDTO.class);
        // 如果从数据库中没有查询到数据，设置空值缓存
        if (CollectionUtils.isEmpty(giftConfigDTOS)) {
            redisTemplate.opsForList().leftPush(cacheKey, new GiftConfigDTO());
            redisTemplate.expire(cacheKey, 1L, TimeUnit.MINUTES);
            return Collections.emptyList();
        }
        // 从数据库查询到数据，写入Redis
        // 往Redis初始化List时，要上锁，避免重复写入造成数据重复
        // 获取锁
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildGiftListLockCacheKey(), 1, 3L, TimeUnit.SECONDS);
        // 如果获取成功的话向Redis写入数据
        if (Boolean.TRUE.equals(isLock)) {
            redisTemplate.opsForList().leftPushAll(cacheKey, giftConfigDTOS.toArray());
            redisTemplate.expire(cacheKey, 30L, TimeUnit.MINUTES);
        }
        // 返回查询到的数据
        return giftConfigDTOS;
    }


    /***
     * 插入一个礼物的信息
     * @param giftConfigDTO
     */
    @Override
    public void insertOne(GiftConfigDTO giftConfigDTO) {
        GiftConfigPO giftConfigPO = ConvertBeanUtils.convert(giftConfigDTO, GiftConfigPO.class);
        giftConfigPO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        giftConfigMapper.insert(giftConfigPO);
    }


    /***
     * 更新一个礼物的信息
     * @param giftConfigDTO
     */
    @Override
    public void updateOne(GiftConfigDTO giftConfigDTO) {
        GiftConfigPO giftConfigPO = ConvertBeanUtils.convert(giftConfigDTO, GiftConfigPO.class);
        giftConfigMapper.updateById(giftConfigPO);
    }

}
