package cn.bdqn.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadPool {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties threadPoolProperties){
        return new ThreadPoolExecutor(
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
