package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.provider.dao.mapper.IAnchorShopInfoMapper;
import org.qiyu.live.gift.provider.dao.po.AnchorShopInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 主播带货权限配置信息相关业务逻辑操作service接口实现类
 * @Version: 1.0
 */
@Service
public class AnchorShopInfoServiceImpl implements IAnchorShopInfoService {


    @Resource
    private IAnchorShopInfoMapper anchorShopInfoMapper;


    /***
     * 根据主播id查询当前主播的带货商品skuId的集合
     * @param anchorId
     * @return
     */
    @Override
    public List<Long> querySkuIdByAnchorId(Long anchorId) {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getAnchorId,anchorId);
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getSkuId).collect(Collectors.toList());
    }


    /***
     * 查询所有有效的主播id
     * @return
     */
    @Override
    public List<Long> queryAllValidAnchorId() {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getAnchorId).collect(Collectors.toList());
    }
}
