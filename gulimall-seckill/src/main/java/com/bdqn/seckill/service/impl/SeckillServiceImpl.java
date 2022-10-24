package com.bdqn.seckill.service.impl;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.to.mq.SeckillOrderTo;
import cn.bdqn.gulimall.vo.MemberVo;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.bdqn.seckill.feign.CouponFeignService;
import com.bdqn.seckill.feign.ProductSeckillFeignService;
import com.bdqn.seckill.interceptor.LoginUserInterceptor;
import com.bdqn.seckill.service.SeckillService;
import com.bdqn.seckill.to.SeckillSKuRedisTo;
import com.bdqn.seckill.vo.SeckillSessionWithSku;
import com.bdqn.seckill.vo.SeckillSkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    private final String SESSION_CACHE_PREFIX = "seckill:session:";
    private final String SEKILL_SKU_CACHE_PREFIX = "seckill:skuInfo";
    private final String SKU_STOCK_CACHE_SEMAPHORE = "seckill:stock:";

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductSeckillFeignService productSeckillFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void uploadSeckillSessionSkuLetes3Day() {
        // 扫描3天需要上架到秒杀商品
        R r = couponFeignService.getLetes3DaySession();
        if (r.getCode() == 0) {
            // 上架商品
            List<SeckillSessionWithSku> data = r.getData(new TypeReference<List<SeckillSessionWithSku>>() {
            });
            //1缓存活动信息
            saveSeckillSession(data);
            //2缓存活动的商品信息
            saveSeckillSessionSkuInfo(data);
        }
    }

    public List<SeckillSKuRedisTo> blockHandler(){
        log.error("getCurrentSeckillSkuInfoResource被限流了，哇额哇 blick");
        return null;
    }

    public List<SeckillSKuRedisTo> currentSeckillFallback(){
        log.error("getCurrentSeckillSkuInfoResource被限流了，哇额哇 fallback");
        return null;
    }

    /**
     * 使用注解方式
     * 无论是代码还是注解，一定要配置默认返回，url请求已经写了一个配置做处理了
     * blockHandler 指定的方法必须在本类中
     * fallback 可以在其他类中，但是类必须是的方法必须静态方法 ,fallbackClass =  如果两个同时出现，那么会先运行fallback方法
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkuInfoResource",blockHandler = "blockHandler",
    fallback = "currentSeckillFallback")
    @Override
    public List<SeckillSKuRedisTo> getCurrentSeckillSkuInfo() {
        // 获取当前时间
        long time = new Date().getTime();
        //获取redis中的存入的key   keys 键（前缀） *
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        //自定义保护资源，受保护的方式
        try (Entry entry = SphU.entry("eckillSkuInfo")) {
            for (String key : keys) {
                //把前缀去掉，在分割得到开始时间和结束时间  seckill:session:1666170000000_1666263600000
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long startTime = Long.parseLong(s[0]);
                long endTime = Long.parseLong(s[1]);
                // 1666170000000	1666173345221	1666263600000
                //1666170000000	1666173590024	1666263600000
                if (startTime <= time && time <= endTime) {
                    //在查询对应的商品信息
                    List<String> range = redisTemplate.opsForList().range(key, 0, -1);
                    //绑定hash操作查询里面的商品信息 泛型是string，因为设置的时候就是string设置进去的
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SEKILL_SKU_CACHE_PREFIX);
                    List<String> seckillSkuInfos = hashOps.multiGet(range);
                    if (seckillSkuInfos != null && seckillSkuInfos.size() > 0) {
                        List<SeckillSKuRedisTo> collect = seckillSkuInfos.stream().map(item -> {
                            // 将json串转成一个对象SeckillSKuRedisTo
                            SeckillSKuRedisTo redisTo = JSON.parseObject(item, SeckillSKuRedisTo.class);
                            return redisTo;
                        }).collect(Collectors.toList());
                        return collect;
                    }

                    break;
                }
            }

        } catch (BlockException e) {
            log.error("资源被限流了{}", e.getMessage());
        }

        return null;
    }

    @Override
    public SeckillSKuRedisTo getSeckillSkuInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SEKILL_SKU_CACHE_PREFIX);
        // 拿到所有的key
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                boolean matches = Pattern.matches(regx, key);
                if (matches) {
                    // 获取值
                    String s = hashOps.get(key);
                    SeckillSKuRedisTo seckillSKuRedisTo = JSON.parseObject(s, SeckillSKuRedisTo.class);
                    // 判断随机码 是否已经可以秒杀，可以1秒杀了那就显示，否则就隐藏
                    long time = new Date().getTime();
                    /*if (time>=seckillSKuRedisTo.getStartTime()&& time<=seckillSKuRedisTo.getEndTime()) {

                    } else {
                        seckillSKuRedisTo.setRandomCode(null);
                    }*/
                    return seckillSKuRedisTo;
                }

            }
        }
        return null;
    }

    //TODO 上架商品的时候，每个都有过期时间
    @Override
    public String kill(String killId, String key, String num) {
        long l = System.currentTimeMillis();
        MemberVo memberVo = LoginUserInterceptor.threadLocal.get();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SEKILL_SKU_CACHE_PREFIX);
        // 获取当前秒杀的详细信息
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            //查询的秒杀详细不为空
            SeckillSKuRedisTo redisTo = JSON.parseObject(json, SeckillSKuRedisTo.class);
            //验证合法性
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            //过期时间差
            long ttl = endTime - time;

            //1验证时间
            if (time >= startTime && time <= endTime) {
                //秒杀活动还在进行中
                //验证killId和随机码
                String randomCode = redisTo.getRandomCode();
                String killid = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if (randomCode.equals(key) && killId.equals(killid)) {
                    //验证成功
                    //验证数量是否合理
                    if (Integer.parseInt(num) <= redisTo.getSeckillLimit()) {
                        //判断这个人是否已经买个，幂等性 userid_sessionid_skuid 并设置过期时间
                        String seckillKey = memberVo.getId() + "_" + killid;
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(seckillKey, num, ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            //占位成功说明没有买个
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_CACHE_SEMAPHORE + key);
                            try {
                                //尝试100ms内抢到信号量
                                boolean b = semaphore.tryAcquire(redisTo.getSeckillLimit(), 100, TimeUnit.MILLISECONDS);
                                if (b) {
                                    //说明秒杀成功了，快速下单，给mq发送消息
                                    String orderSn = IdWorker.getTimeId();
                                    SeckillOrderTo to = new SeckillOrderTo();
                                    to.setOrderSn(orderSn);
                                    to.setMemberId(memberVo.getId());
                                    to.setNum(redisTo.getSeckillLimit());
                                    to.setPromotionSessionId(redisTo.getPromotionSessionId());
                                    to.setSeckillPrice(redisTo.getSeckillPrice());
                                    to.setSkuId(redisTo.getSkuId());
                                    rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", to);
                                    long l1 = System.currentTimeMillis();
                                    long l2 = l1 - l;
                                    System.out.println("耗费：" + l2);
                                    return orderSn;
                                } else {
                                    return null;
                                }
                            } catch (InterruptedException e) {
                                return null;
                            }

                        } else {
                            //这个人已经买过了
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    //验证失败
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    //1缓存活动信息
    private void saveSeckillSession(List<SeckillSessionWithSku> data) {
        if (data != null && data.size() > 0) {

            data.forEach(session -> {
                //以开始时间和结束时间作为key 转成long值，方便就计算
                long startTime = session.getStartTime().getTime();
                long endTime = session.getEndTime().getTime();
                String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
                if (!redisTemplate.hasKey(key)) {
                    List<String> collect = session.getRelationSkus().stream().map(item -> {
                        return item.getPromotionSessionId() + "_" + item.getSkuId().toString();
                    }).collect(Collectors.toList());
                    // 从左到右依次将list添加到redis中
                    if (collect != null && collect.size() > 0) {
                        redisTemplate.opsForList().leftPushAll(key, collect);
                    }
                }
            });
        }
    }

    //2缓存活动的商品信息
    private void saveSeckillSessionSkuInfo(List<SeckillSessionWithSku> data) {
        if (data != null && data.size() > 0) {

            data.forEach(session -> {
                // 绑定一个hash
                BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SEKILL_SKU_CACHE_PREFIX);
                session.getRelationSkus().forEach(item -> {
                    //4给每一个商品设置一个随机码（用于防止开放人员使用工具秒杀到商品），
                    String token = UUID.randomUUID().toString().replace("-", "");
                    if (!hashOps.hasKey(item.getPromotionSessionId() + "_" + item.getSkuId().toString())) {
                        //缓存商品信息
                        SeckillSKuRedisTo redisTo = new SeckillSKuRedisTo();
                        //1sku的基本信息
                        R r = productSeckillFeignService.getSkuInfo(item.getSkuId());
                        if (r.getCode() == 0) {
                            SeckillSkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SeckillSkuInfoVo>() {
                            });
                            redisTo.setSeckillSkuInfoVo(skuInfo);
                        }

                        //2秒杀商品的信息
                        BeanUtils.copyProperties(item, redisTo);

                        //3设置当前商品的秒杀开始时间和过期时间
                        redisTo.setStartTime(session.getStartTime().getTime());
                        redisTo.setEndTime(session.getEndTime().getTime());

                        redisTo.setRandomCode(token);

                        String jsonString = JSONObject.toJSONString(redisTo);
                        hashOps.put(item.getPromotionSessionId() + "_" + item.getSkuId().toString(), jsonString);


                        //5使用库存作为分布式信号量，限流
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_CACHE_SEMAPHORE + token);
                        // 以商品秒杀的数量作为分布式信号量
                        semaphore.trySetPermits(item.getSeckillCount());
                    }
                });
            });
        }
    }
}
