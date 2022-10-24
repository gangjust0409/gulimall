package com.bdqn.seckill.feign;

import cn.bdqn.gulimall.common.utils.R;
import com.bdqn.seckill.feign.fallback.ProductSeckillFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimall-product",fallback = ProductSeckillFeignFallback.class)
@Component
public interface ProductSeckillFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

}
