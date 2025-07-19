package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 礼物服务模块Redis中key值的生成器
 * @Version: 1.0
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class GiftProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的送礼服务中mq消息消费记录的key值 */
    private static String GIFT_CONSUME_KEY = "gift_consume_key";


    /** 存入Redis的直播间pk进度的缓存的key值 */
    private static String LIVING_PK_KEY = "living_pk_key";


    /** 存入Redis的当前pk消息的序列号的缓存的key值 */
    private static String LIVING_PK_SEND_SEQ = "living_pk_send_seq";


    /** 存入Redis的直播间所有礼物的列表的缓存的key值 */
    private static String GIFT_LIST_CACHE = "gift_list_cache";


    /** 向Redis中写入直播间所有礼物列表时的锁对象 */
    private static String GIFT_LIST_LOCK = "gift_list_lock";


    /** 向Redis中记录当前pk直播间是否结束的状态的缓存的key值 */
    private static String LIVING_PK_IS_OVER = "living_pk_is over";


    /** 向Redis中存储红包雨红包数据的list集合的缓存的key值 */
    private static String RED_PACKET_LIST = "red_packet_list";


    /** 防止在多线程环境下生成多个红包雨红包数据的加锁操作的锁的key值 */
    private static String RED_PACKET_INIT_LOCK = "red_packet_init_lock";


    /** 向Redis中存储的抢红包过程中记录当前总共分发了多少红包数量的缓存的key值 */
    private static String RED_PACKET_TOTAL_GET_COUNT = "red_packet_total_get_count";


    /** 向Redis中存储的抢红包过程中记录当前总共分发了多少红包金额的缓存的key值 */
    private static String RED_PACKET_TOTAL_GET_PRICE = "red_packet_total_get_price";


    /** 向Redis中存储的抢红包过程中记录当前分发的最大红包金额的缓存的key值 */
    private static String RED_PACKET_MAX_GET_PRICE = "red_packet_max_get_price";


    /** 向Redis中存储的当前用户在本次抢红包活动中总共领取到的红包金额的缓存的key值 */
    private static String USER_TOTAL_GET_PRICE_CACHE = "red_packet_user_total_get_price";


    /** 向Redis中存储的当前红包雨的红包数据初始化完成的标志的缓存的key值 */
    private static String RED_PACKET_PREPARE_SUCCESS = "red_packet_prepare_success";


    /** 向Redis中存储的开始抢红包活动后的广播行为完成的标志的缓存的key值 */
    private static String RED_PACKET_NOTIFY = "red_packet_notify";


    /** 向Redis中存储商品详情信息配置的缓存的key值 */
    private static String SKU_DETAIL= "sku_detail";


    /** 向Redis中存储用户在当前直播间中的购物车基本信息的缓存的key值 */
    private static String SHOP_CAR = "shop_car";


    /** 向Redis中存储当前直播间中的秒杀商品的库存信息的缓存的key值 */
    private static String SKU_STOCK = "sku_stock";


    /** 操作从Redis中更新商品缓存到MySQL中时防止多线程环境下数据错乱加入的锁的key值 */
    private static String STOCK_SYNC_LOCK = "stock_sync_lock";


    /** 向Redis中存储商品订单信息的缓存的key值(以userId和roomId为维度) */
    private static String SKU_ORDER = "sku_order";


    /** 向Redis中存储商品订单信息的缓存的key值(以orderId为维度) */
    private static String SKU_ORDER_INFO = "sku_order_info";


    /** 存入Redis的送礼服务中mq消息消费记录的key值 */
    public String buildGiftConsumeKey(String uuid) {
        return super.getPrefix() + GIFT_CONSUME_KEY + super.getSplitItem() + uuid;
    }


    /** 存入Redis的直播间pk进度的缓存的key值 */
    public String buildLivingPkKey(Integer roomId) {
        return super.getPrefix() + LIVING_PK_KEY + super.getSplitItem() + roomId;
    }


    /** 存入Redis的当前pk消息的序列号的缓存的key值 */
    public String buildLivingPkSendSeq(Integer roomId) {
        return super.getPrefix() + LIVING_PK_SEND_SEQ + super.getSplitItem() + roomId;
    }


    /** 存入Redis的直播间所有礼物的列表的缓存的key值 */
    public String buildGiftListCacheKey() {
        return super.getPrefix() + GIFT_LIST_CACHE;
    }


    /** 向Redis中写入直播间所有礼物列表时的锁对象 */
    public String buildGiftListLockCacheKey() {
        return super.getPrefix() + GIFT_LIST_LOCK;
    }


    /** 向Redis中记录当前pk直播间是否结束的状态的缓存的key值 */
    public String buildLivingPkIsOver(Integer roomId) {
        return super.getPrefix() + LIVING_PK_IS_OVER + super.getSplitItem() + roomId;
    }


    /** 向Redis中存储红包雨红包数据的list集合的缓存的key值 */
    public String buildRedPacketList(String code) {
        return super.getPrefix() + RED_PACKET_LIST + super.getSplitItem() + code;
    }


    /** 防止在多线程环境下生成多个红包雨红包数据的加锁操作的锁的key值 */
    public String buildRedPacketInitLock(String code) {
        return super.getPrefix() + RED_PACKET_INIT_LOCK + super.getSplitItem() + code;
    }


    /** 向Redis中存储的抢红包过程中记录当前总共分发了多少红包数量的缓存的key值 */
    public String buildRedPacketTotalGetCount(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_COUNT + super.getSplitItem() + code;
    }


    /** 向Redis中存储的抢红包过程中记录当前总共分发了多少红包金额的缓存的key值 */
    public String buildRedPacketTotalGetPrice(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_PRICE + super.getSplitItem() + code;
    }


    /** 向Redis中存储的抢红包过程中记录当前分发的最大红包金额的缓存的key值 */
    public String buildRedPacketMaxGetPrice(String code) {
        return super.getPrefix() + RED_PACKET_MAX_GET_PRICE + super.getSplitItem() + code;
    }


    /** 向Redis中存储的当前用户在本次抢红包活动中总共领取到的红包金额的缓存的key值 */
    public String buildUserTotalGetPrice(Long userId) {
        return super.getPrefix() + USER_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + userId;
    }


    /** 向Redis中存储的当前红包雨的红包数据初始化完成的标志的缓存的key值 */
    public String buildRedPacketPrepareSuccess(String code) {
        return super.getPrefix() + RED_PACKET_PREPARE_SUCCESS + super.getSplitItem() + code;
    }


    /** 向Redis中存储的开始抢红包活动后的广播行为完成的标志的缓存的key值 */
    public String buildRedPacketNotify(String code) {
        return super.getPrefix() + RED_PACKET_NOTIFY + super.getSplitItem() + code;
    }


    /** 向Redis中存储商品详情信息配置的缓存的key值 */
    public String buildSkuDetail(Long skuId) {
        return super.getPrefix() + SKU_DETAIL + super.getSplitItem() + skuId;
    }


    /** 向Redis中存储用户在当前直播间中的购物车基本信息的缓存的key值 */
    public String buildUserShopCar(Long userId, Integer roomId) {
        return super.getPrefix() + SHOP_CAR + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }


    /** 向Redis中存储当前直播间中的秒杀商品的库存信息的缓存的key值 */
    public String buildSkuStock(Long skuId) {
        return super.getPrefix() + SKU_STOCK + super.getSplitItem() + skuId;
    }


    /** 操作从Redis中更新商品缓存到MySQL中时防止多线程环境下数据错乱加入的锁的key值 */
    public String buildStockSyncLock() {
        return super.getPrefix() + STOCK_SYNC_LOCK;
    }


    /** 向Redis中存储商品订单信息的缓存的key值 */
    public String buildSkuOrder(Long userId,Integer roomId) {
        return super.getPrefix() + SKU_ORDER + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }


    /** 向Redis中存储商品订单信息的缓存的key值(以orderId为维度) */
    public String buildSkuOrderInfo(Long orderId) {
        return super.getPrefix() + SKU_ORDER_INFO + super.getSplitItem() + orderId;
    }

}
