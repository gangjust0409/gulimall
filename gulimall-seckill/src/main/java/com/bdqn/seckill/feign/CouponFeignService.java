package com.bdqn.seckill.feign;

import cn.bdqn.gulimall.common.utils.R;
import com.bdqn.seckill.feign.fallback.CouponFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "gulimall-coupon",fallback = CouponFeignFallback.class)
@Component
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/letes3DaySession")
    R getLetes3DaySession();

}
