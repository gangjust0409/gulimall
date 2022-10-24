package com.bdqn.seckill.service;

import com.bdqn.seckill.to.SeckillSKuRedisTo;

import java.util.List;

public interface SeckillService {
    // 上架秒杀商品
    void uploadSeckillSessionSkuLetes3Day();

    // 获取当前时间段的商品信息
    List<SeckillSKuRedisTo> getCurrentSeckillSkuInfo();

    // 查询当前商品是否参与秒杀
    SeckillSKuRedisTo getSeckillSkuInfo(Long skuId);

    //秒杀方法
    String kill(String killId, String key, String num);
}
