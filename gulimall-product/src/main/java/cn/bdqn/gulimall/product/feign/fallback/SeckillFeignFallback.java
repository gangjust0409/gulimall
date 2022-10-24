package cn.bdqn.gulimall.product.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignFallback implements SeckillFeignService {
    @Override
    public R getSeckillSkuInfo(Long skuId) {
        log.info("服务熔断 getSeckillSkuInfo...");
        R r = R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(), BizExceptionCode.NO_MANY_REQUEST.getMsg());
        return r;
    }
}
