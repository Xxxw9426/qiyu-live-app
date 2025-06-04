package org.qiyu.live.living.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.provider.dao.mapper.LivingRoomMapper;
import org.qiyu.live.living.provider.dao.mapper.LivingRoomRecordMapper;
import org.qiyu.live.living.provider.dao.po.LivingRoomPO;
import org.qiyu.live.living.provider.dao.po.LivingRoomRecordPO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关正在直播的直播间的相关操作的service接口实现类
 * @Version: 1.0
 */
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {


    @Resource
    private LivingRoomMapper livingRoomMapper;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;


    @Resource
    private LivingProviderCacheKeyBuilder livingProviderCacheKeyBuilder;


    /***
     * 开播的逻辑操作
     * 封装一个正在直播的直播间数据库表po对象并且插入数据库中
     * @param livingRoomReqDTO
     * @return  返回开播的直播间的id
     */
    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        // 封装要插入数据库中的数据的对象
        LivingRoomPO livingRoomPO= ConvertBeanUtils.convert(livingRoomReqDTO,LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(new Date());
        // 插入
        livingRoomMapper.insert(livingRoomPO);
        // 防止之前有空值缓存，这里做移除操作
        String cacheKey=livingProviderCacheKeyBuilder.buildLivingRoomObj(livingRoomPO.getId());
        redisTemplate.delete(cacheKey);
        return livingRoomPO.getId();
    }


    /**
     * 关闭直播间
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        // 首先根据直播间id查询直播间数据
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(livingRoomReqDTO.getRoomId());
        // 没有查找到说明当前直播间并没有开播
        if(livingRoomPO==null) {
            return false;
        }
        // 然后判断当前token对应的用户id与要关闭直播间对应的主播id是否相同
        // 如果不相同的话,说明没有权限
        if(!livingRoomPO.getAnchorId().equals(livingRoomReqDTO.getAnchorId())) {
            return false;
        }
        // 校验结束后
        // 删除正在直播的数据库表中的数据
        livingRoomMapper.deleteById(livingRoomPO.getId());
        // 向直播记录数据库表中添加记录
        LivingRoomRecordPO livingRoomRecordPO=ConvertBeanUtils.convert(livingRoomPO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordPO.setStatus(CommonStatusEnum.INVALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        // 移除直播间缓存
        String cacheKey=livingProviderCacheKeyBuilder.buildLivingRoomObj(livingRoomPO.getId());
        redisTemplate.delete(cacheKey);
        return true;
    }


    /**
     * 根据直播间id查询直播间相关信息
     * 之前的思路是直接从数据库中查询
     * 优化后我们思路也是通过Redis缓存进行查询
     * @param roomId
     * @return
     */
    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        // 首先从Redis缓存中查询当前直播间的相关数据
        String cacheKey=livingProviderCacheKeyBuilder.buildLivingRoomObj(roomId);
        LivingRoomRespDTO queryResult=(LivingRoomRespDTO) redisTemplate.opsForValue().get(cacheKey);
        // 如果缓存命中
        if(queryResult!=null) {
            // 判断是否是空值缓存
            if(queryResult.getId()==null) {
                return null;
            }
            return queryResult;
        }
        // 缓存未命中
        // 查询数据库中对应直播间id的直播间数据
        LambdaQueryWrapper<LivingRoomPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getId,roomId);
        queryWrapper.eq(LivingRoomPO::getStatus,CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        queryResult=ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper),LivingRoomRespDTO.class);
        // 如果数据库也没有查询到
        if(queryResult==null) {
            // 防止缓存击穿，设置空值
            redisTemplate.opsForValue().set(cacheKey,new LivingRoomRespDTO(),1, TimeUnit.MINUTES);
            return null;
        }
        // 数据库查询到的话，设置到缓存中并返回
        redisTemplate.opsForValue().set(cacheKey,queryResult,30,TimeUnit.MINUTES);
        return queryResult;
    }


    /***
     * 直播间列表的分页查询和展示
     * 之前的思路是直接从数据库中进行分页查询，
     * 优化后我们会将当前正在直播的直播间缓存到Redis中的一个list集合中，当我们需要查询直播间列表的时候直接从缓存的集合中通过range进行查询即可
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        // 设置缓存直播间列表的key值
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomList(livingRoomReqDTO.getType());
        int page=livingRoomReqDTO.getPage();
        int pageSize=livingRoomReqDTO.getPageSize();
        long total=redisTemplate.opsForList().size(cacheKey);
        List<Object> resultList=redisTemplate.opsForList().range(cacheKey,(page-1)*pageSize,(page*pageSize));
        // 构建我们要返回的结果类
        PageWrapper<LivingRoomRespDTO> pageWrapper=new PageWrapper<>();
        if (CollectionUtils.isEmpty(resultList)) {
            pageWrapper.setList(Collections.emptyList());
            pageWrapper.setHasNext(false);
        } else {
            pageWrapper.setList(ConvertBeanUtils.convertList(resultList, LivingRoomRespDTO.class));
            pageWrapper.setHasNext((long) page * pageSize < total);
        }
        return pageWrapper;
    }


    /***
     * 从数据库中查询所有的正在直播的直播间列表
     * @param type
     * @return
     */
    @Override
    public List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type) {
        // 设置查询参数
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getType, type);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        // 按照时间倒序展示
        queryWrapper.orderByDesc(LivingRoomPO::getStartTime);
        // 为性能做保证，默认只查询1000个
        queryWrapper.last("limit 1000");
        return ConvertBeanUtils.convertList(livingRoomMapper.selectList(queryWrapper), LivingRoomRespDTO.class);
    }


    /***
     * 将消息队列中监听到的用户加入直播间的消息中的用户id记录到当前直播间的用户集合中
     * @param imOnlineDTO
     */
    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        Long userId = imOnlineDTO.getUserId();
        Integer roomId = imOnlineDTO.getRoomId();
        Integer appId = imOnlineDTO.getAppId();
        // 存入Redis的当前直播间在线用户列表的set集合中
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        redisTemplate.opsForSet().add(cacheKey,userId);
        redisTemplate.expire(cacheKey,12,TimeUnit.HOURS);
    }


    /***
     * 当消息队列中监听到用户退出直播间的消息后将用户id从当前直播间的用户集合中移除
     * @param imOfflineDTO
     */
    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        Long userId = imOfflineDTO.getUserId();
        Integer roomId = imOfflineDTO.getRoomId();
        Integer appId = imOfflineDTO.getAppId();
        // 从Redis中的当前直播间在线用户列表的set集合中移除
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        redisTemplate.opsForSet().remove(cacheKey,userId);
    }


    /***
     * 根据直播间的roomId和appId批量的查询当前直播间中的所有在线用户id集合
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Integer appId = livingRoomReqDTO.getAppId();
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        // 使用scan每次100条100条的读取数据，提高读取数据的效率并且减小遍历对于redis的压力
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(cacheKey, ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList=new ArrayList<Long>();
        // 遍历获取出来的100条数据加入最终的结果
        while(cursor.hasNext()) {
            Long userId = (Long) cursor.next();
            userIdList.add(userId);
        }
        return userIdList;
    }
}
