package com.bdqn.seckill.to;

import com.bdqn.seckill.vo.SeckillSkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSKuRedisTo {


    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;

    /**
     * 商品随机码
     */
    private String randomCode;

    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    // 商品的基本信息
    private SeckillSkuInfoVo seckillSkuInfoVo;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

}
