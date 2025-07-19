package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.mapper.ISkuStockInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.qiyu.live.gift.provider.service.bo.DecrStockNumBO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品库存相关业务逻辑操作service接口实现类
 * @Version: 1.0
 */
@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {


    @Resource
    private ISkuStockInfoMapper skuStockInfoMapper;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Resource
    private ISkuOrderInfoService skuOrderInfoService;


    // lua脚本代码(初)
    private String LUA_SCRIPT = "if (redis.call('exists', KEYS[1])) == 1 then" +
            "    local currentStock = redis.call('get', KEYS[1])" +
            "    if (tonumber(currentStock) >= tonumber(ARGV[1])) then" +
            "        return redis.call('decrby', KEYS[1], tonumber(ARGV[1]))" +
            "    else" +
            "        return -1" +
            "    end" +
            "    return -1" +
            "end";


    // lua脚本代码(终)
    private String BATCH_LUA_SCRIPT="for i=1,ARGV[2] do \n"+
            "     if (redis.call('exists', KEYS[i])) ~= 1 then return -1 end \n" +
            "\tlocal currentStock=redis.call('get',KEYS[i]) \n"+
            "\tif (tonumber(currentStock)<=0 and tonumber(currentStock)-tonumber(ARGV[1])<0) then\n"+
            "       return -1\n"+
            "\tend\n"+
            "end \n"+
            "\n"+
            "for j=1,ARGV[2] do \n"+
            "\tredis.call('decrby',KEYS[j],tonumber(ARGV[1]))\n"+
            "end \n"+
            "return 1";


    /***
     * 根据商品skuId更新商品库存
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public DecrStockNumBO decrStockNumBySkuId(Long skuId, Integer num) {
        // 首先查询出我们库存的当前版本号
        SkuStockInfoPO skuStockInfoPO = this.queryBySkuId(skuId);
        DecrStockNumBO decrStockNumBO=new DecrStockNumBO();
        if(skuStockInfoPO.getStockNum()==0 || skuStockInfoPO.getStockNum()-num <0) {
            decrStockNumBO.setNoStock(true);
            decrStockNumBO.setSuccess(false);
            return decrStockNumBO;
        }
        boolean updateStatus=skuStockInfoMapper.decrStockNumBySkuId(skuId,1,skuStockInfoPO.getVersion())>0;
        decrStockNumBO.setNoStock(false);
        decrStockNumBO.setSuccess(updateStatus);
        return decrStockNumBO;
    }


    /***
     * 根据skuId查询商品的库存信息
     * @param skuId
     * @return
     */
    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuStockInfoPO::getSkuId,skuId);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuStockInfoMapper.selectOne(queryWrapper);
    }


    /***
     * 根据传入的商品skuId的集合查询其对应的库存
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuStockInfoPO::getSkuId,skuIds);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return skuStockInfoMapper.selectList(queryWrapper);
    }


    /***
     * 更新商品库存为传入的num值
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean updateStockNum(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = new SkuStockInfoPO();
        skuStockInfoPO.setStockNum(num);
        LambdaUpdateWrapper<SkuStockInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SkuStockInfoPO::getSkuId,skuId);
        skuStockInfoMapper.update(skuStockInfoPO,updateWrapper);
        return true;
    }


    /***
     * 根据商品skuId更新商品库存版本2
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean decrStockNumBySkuIdV2(Long skuId, Integer num) {
        // 首先从Redis中获取到缓存的商品库存的值
        // 然后判断当前库存的值是否还能被扣减成功
        // 如果可以的话进行扣减
        // Redis中专门提供的用来向Redis传输我们的Lua脚本的方法
        // 这里的泛型指的是我们的Lua脚本执行完后返回的内容
        DefaultRedisScript<Long> redisScript=new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        // 向Lua脚本传参
        // 1. 拿取我们设置的Redis中库存的key值 2. 扣减的库存值num
        String skuStockCacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        // 执行Lua脚本
        return redisTemplate.execute(redisScript, Collections.singletonList(skuStockCacheKey),num)>=0;
    }


    /***
     * 根据商品的skuId的集合更新库存版本3
     * @param skuIdList
     * @param num
     * @return
     */
    @Override
    public boolean decrStockNumBySkuIdV3(List<Long> skuIdList, Integer num) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(BATCH_LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        List<String> skuStockCacheKeyList=new ArrayList<>();
        for(Long skuId:skuIdList){
            String skuStockCacheKey = cacheKeyBuilder.buildSkuStock(skuId);
            skuStockCacheKeyList.add(skuStockCacheKey);
        }
        return redisTemplate.execute(redisScript, skuStockCacheKeyList, num,skuStockCacheKeyList.size()) >= 0;
    }


    /***
     * 库存回滚操作处理器
     * @param rollBackStockDTO
     */
    @Override
    public void stockRollBackHandler(RollBackStockDTO rollBackStockDTO) {
        // 首先根据订单id查询到订单的信息
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = skuOrderInfoService.queryByOrderId(rollBackStockDTO.getOrderId());
        // 如果订单信息为空或者订单装备不是待支付状态
        if(skuOrderInfoRespDTO==null || !skuOrderInfoRespDTO.getStatus().equals(SkuOrderInfoEnum.PREPARE_PAY.getCode())) {
            return;
        }
        // 如果此时订单状态仍然为待支付状态的话，将订单状态修改为超时未支付状态(取消订单状态)并且进行库存回滚
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.CANCEL.getCode());
        skuOrderInfoReqDTO.setId(rollBackStockDTO.getOrderId());
        // 设置订单状态为撤销状态
        skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
        // 回滚库存
        // 拿到订单中的所有商品的id的集合
        List<Long> skuIdList = Arrays.stream(skuOrderInfoRespDTO.getSkuIdList().split(",")).map(Long::valueOf).collect(Collectors.toList());
        // 遍历并且直接增加
        skuIdList.parallelStream().forEach(skuId -> {
            // 只用更新Redis库存，定时任务会自动更新MySQL库存
            redisTemplate.opsForValue().increment(cacheKeyBuilder.buildSkuStock(skuId), 1);
        });
    }
}
