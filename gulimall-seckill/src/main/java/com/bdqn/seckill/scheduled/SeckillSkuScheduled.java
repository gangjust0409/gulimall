package com.bdqn.seckill.scheduled;

import com.bdqn.seckill.service.SeckillService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String UPLOAD_LOCK="upload:lock";

    /**
     * 保证幂等性
     */
    @Async
    @Scheduled(cron = "*/5 * * * * ?")
    public void uploadSkuLetes3Day() {
        // 重复上架可以不用处理
        // 获取3天的活动
        // 加分布式锁，状态已经更新，释放锁，其他人拿到的最新的
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSessionSkuLetes3Day();
        } finally {
            lock.unlock();
        }
        System.out.println("自动上架成功！");
    }

}
