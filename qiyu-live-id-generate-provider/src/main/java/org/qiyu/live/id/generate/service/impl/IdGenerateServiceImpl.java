package org.qiyu.live.id.generate.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.id.generate.dao.mapper.IdGenerateMapper;
import org.qiyu.live.id.generate.dao.po.IdGeneratePO;
import org.qiyu.live.id.generate.service.IdGenerateService;
import org.qiyu.live.id.generate.service.bo.LocalSeqIdBO;
import org.qiyu.live.id.generate.service.bo.LocalUnSeqIdBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: 分布式id生成器service接口实现类
 * @Version: 1.0
 */

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {


    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateServiceImpl.class);


    // 存储有序id生成策略及生成器的map
    private static Map<Integer,LocalSeqIdBO> localSeqIdBOMap=new ConcurrentHashMap<>();


    // 存储无序id生成策略及生成器的map
    private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdBOMap = new ConcurrentHashMap<>();


    // 定义内存中id段更新的阈值为75%
    private static final float UPDATE_RATE = 0.75f;


    private static final int SEQ_ID = 1;


    @Resource
    private IdGenerateMapper idGenerateMapper;


    /**
     * Semaphore：java并发包中的限流组件
     * 在我们的程序中，当我们的id段的值大于75%之后，如果我们的更新操作没有那么及时的话，
     * 后序还会有一部分更新完成之前的请求也满足大于75%条件进入更新内存id段的方法中，这就可能会导致重复更新
     * 因此我们可以使用Semaphore来实现当我们在执行内存中id段的更新的时候，防止其他任务提交到线程池中被执行
     */
    private static Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();



    // 创建线程池来异步的执行更新内存中id段的方法，而不是与获取id同步执行
    // 同步执行会有一部分网络io的消耗，导致我们返回id的方法耗时过多
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt(1000));
                    return thread;
                }
            });


    /***
     * 获取有序的分布式Id的Rpc接口
     * @param id  该参数对应的是我们数据库表中的主键id，根据这个id我们可以查询到不同的id生成策略
     * @return
     */
    @Override
    public Long getSeqId(Integer id) {
        // 首先对传入参数进行校验
        if (id == null) {
            LOGGER.error("[getSeqId] id is error,id is {}", id);
            return null;
        }
        // 从内存中获取到我们当前最新可用的有序id
        LocalSeqIdBO localSeqIdBO=localSeqIdBOMap.get(id);
        if (localSeqIdBO == null) {
            LOGGER.error("[getSeqId] localSeqIdBO is null,id is {}", id);
            return null;
        }
        // 判断并执行更新内存中的id段
        this.refreshLocalSeqId(localSeqIdBO);
        // 对当前id进行获取并自增的操作
        long returnId=localSeqIdBO.getCurrentNum().getAndIncrement();
        // 再次判断如果当前id值已经大于了当前字段的上限则返回空
        if(returnId>localSeqIdBO.getNextThreshold()) {
            LOGGER.error("[getSeqId] localSeqIdBO is over limit,id is {}", id);
            return null;
        }
        return returnId;
    }

    /***
     * 获取无序的分布式Id的Rpc接口
     * @param id
     * @return
     */
    @Override
    public Long getUnSeqId(Integer id) {
        // 首先对传入参数进行校验
        if (id == null) {
            LOGGER.error("[getUnSeqId] id is error,id is {}", id);
            return null;
        }
        // 从内存中获取到我们当前最新可用的有序id
        LocalUnSeqIdBO localUnSeqIdBO=localUnSeqIdBOMap.get(id);
        if (localUnSeqIdBO == null) {
            LOGGER.error("[getUnSeqId] localUnSeqIdBO is null,id is {}", id);
            return null;
        }
        Long returnId=localUnSeqIdBO.getIdQueue().poll();
        if (returnId == null) {
            LOGGER.error("[getUnSeqId] returnId is null,id is {}", id);
            return null;
        }
        // 刷新本地无序id段的方法
        this.refreshLocalUnSeqId(localUnSeqIdBO);
        return returnId;
    }


    /***
     * 初始化我们的存储和生成id值的map
     * @throws Exception
     */
    // 当spring容器在初始化当前类bean时会回调这个方法
    @Override
    public void afterPropertiesSet() throws Exception {
        // 在这个方法中初始化我们的map
        // 从数据库中查询我们不同id生成策略对应的当前的id值
        List<IdGeneratePO> idGeneratePOList = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO : idGeneratePOList) {
            tryUpdateMySQLRecord(idGeneratePO);
            semaphoreMap.put(idGeneratePO.getId(), new Semaphore(1));
        }
    }


    /***
     * 加载map中的数据同时更新数据库表(含重试机制)的方法
     * @param idGeneratePO
     */
    private void tryUpdateMySQLRecord(IdGeneratePO idGeneratePO) {

        int updateResult=idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
        // 如果更新数据库更新成功，将查询到的数据封装到map中
        if(updateResult>0){
            localIdBOHandler(idGeneratePO);
            return;
        }
        // 如果更新失败说明有其他线程正在进行更新操作，则此时我们进行重试操作的时候就需要重新获取一次数据库中当前的数据
        for(int i=0;i<3;i++) {
            idGeneratePO=idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult=idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
            // 查询成功则进行更新map操作
            if(updateResult>0){
                localIdBOHandler(idGeneratePO);
                return;
            }
        }
        throw new RuntimeException("表id段占用失败，竞争过于激烈，id is " + idGeneratePO.getId());
    }


    /***
     * 专门处理如何将本地id对象放入到map中，并且进行初始化的方法
     * @param idGeneratePO
     */
    private void localIdBOHandler(IdGeneratePO idGeneratePO) {

        long currentStart=idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        long currentNum=currentStart;
        // 如果当前id生成策略是有序，将其放入有序id的map中
        if(idGeneratePO.getIsSeq()==SEQ_ID) {
            LocalSeqIdBO localSeqIdBO=new LocalSeqIdBO();
            AtomicLong atomicLong=new AtomicLong(currentNum);
            localSeqIdBO.setId(idGeneratePO.getId());
            localSeqIdBO.setCurrentNum(atomicLong);
            localSeqIdBO.setCurrentStart(currentStart);
            localSeqIdBO.setNextThreshold(nextThreshold);
            localSeqIdBOMap.put(idGeneratePO.getId(), localSeqIdBO);

            // 如果当前id生成策略是无序，将其放入无序id的map中
        } else {
            LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
            localUnSeqIdBO.setCurrentStart(currentStart);
            localUnSeqIdBO.setNextThreshold(nextThreshold);
            localUnSeqIdBO.setId(idGeneratePO.getId());
            long begin = localUnSeqIdBO.getCurrentStart();
            long end = localUnSeqIdBO.getNextThreshold();
            // 通过list提供的方法，将我们的区间里的数字加入该list并且打乱
            List<Long> idList = new ArrayList<>();
            for (long i = begin; i < end; i++) {
                idList.add(i);
            }
            //将本地id段提前打乱，然后放入到队列中
            Collections.shuffle(idList);
            ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>();
            idQueue.addAll(idList);
            localUnSeqIdBO.setIdQueue(idQueue);
            localUnSeqIdBOMap.put(localUnSeqIdBO.getId(), localUnSeqIdBO);
        }
    }


    /**
     * 刷新本地有序id段的方法
     * @param localSeqIdBO
     */
    private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
        // 首先获取我们设定的当前id生成策略的每个id段的长度
        long step = localSeqIdBO.getNextThreshold() - localSeqIdBO.getCurrentStart();
        // 如果我们已经使用了超过75%当前id段
        if (localSeqIdBO.getCurrentNum().get() - localSeqIdBO.getCurrentStart() > step * UPDATE_RATE) {
            // 获得semaphoreMap对象来执行限流和拦截
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            if (semaphore == null) {
                LOGGER.error("semaphore is null,id is {}", localSeqIdBO.getId());
                return;
            }
            // 判断当前线程是否可以进入线程池进行异步更新
            boolean acquireStatus = semaphore.tryAcquire();
            // 可以的话执行更新操作
            if (acquireStatus) {
                LOGGER.info("开始尝试进行本地内存id段的同步操作");
                // 开启一个线程池异步的执行更新内存id段
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        } catch (Exception e) {
                            LOGGER.error("[refreshLocalSeqId] error is ", e);
                        } finally {
                            semaphoreMap.get(localSeqIdBO.getId()).release();
                            LOGGER.info("本地有序id段同步完成,id is {}", localSeqIdBO.getId());
                        }
                    }
                });
            }
        }
    }


    /***
     * 刷新本地无序id段的方法
     * @param localUnSeqIdBO
     */
    private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO) {
        long begin = localUnSeqIdBO.getCurrentStart();
        long end = localUnSeqIdBO.getNextThreshold();
        long remainSize = localUnSeqIdBO.getIdQueue().size();
        // 如果使用剩余空间不足25%，则进行刷新
        if ((end - begin) * 0.25 > remainSize) {
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBO.getId());
            if (semaphore == null) {
                LOGGER.error("semaphore is null,id is {}", localUnSeqIdBO.getId());
                return;
            }
            boolean acquireStatus = semaphore.tryAcquire();
            if (acquireStatus) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        } catch (Exception e) {
                            LOGGER.error("[refreshLocalUnSeqId] error is ", e);
                        } finally {
                            semaphoreMap.get(localUnSeqIdBO.getId()).release();
                            LOGGER.info("本地无序id段同步完成，id is {}", localUnSeqIdBO.getId());
                        }
                    }
                });
            }
        }

    }

}
