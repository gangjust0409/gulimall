package com.bdqn.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 开启定时任务  配置类 TaskSchedulingAutoConfiguration
 *      @EnableScheduling
 *      @Scheduled 开启一个定时任务
 *  开启异步任务 配置类 TaskExecutionAutoConfiguration
 *          @EnableAsync  开启异步任务功能
 *          @Async 给希望异步执行这个方法标注
 */
/*@EnableAsync
@EnableScheduling
@Component*/
@Slf4j
public class HelloScheduled {

    /**
     * spring cron 没有年，只能是6位
     * 在周几位置，1-7 周一至周五，也可以写英文
     * 定时任务不应该是阻塞的，默认是阻塞的
     * 开启异步的方式
     *  1  CompletableFuture.runAsync(()->{},ex); 开启一个异步线程
     *  2 内部使用了线程池方式 spring.task.scheduling.pool.size=5
     *  3 让定时任务异步执行
     *
     *  解决：使用异步+定时任务来完成不阻塞的功能
     */
    @Async
    @Scheduled(cron = "* * * ? * *")
    public void testScheduled() throws InterruptedException {
        log.info("打印hello...");
        Thread.sleep(3000);
    }

}
