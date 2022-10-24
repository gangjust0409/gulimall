package com.bdqn.seckill.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import com.bdqn.seckill.feign.ProductSeckillFeignService;
import org.springframework.stereotype.Component;

@Component
public class ProductSeckillFeignFallback implements ProductSeckillFeignService {
    @Override
    public R getSkuInfo(Long skuId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
