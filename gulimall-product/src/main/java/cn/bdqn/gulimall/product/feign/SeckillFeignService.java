package cn.bdqn.gulimall.product.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.product.feign.fallback.SeckillFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimall-seckill", fallback = SeckillFeignFallback.class)
@Component
public interface SeckillFeignService {

    @GetMapping("/sku/seckill/{skuId}")
    R getSeckillSkuInfo(@PathVariable Long skuId);

}
