package cn.bdqn.cart.feign.fallback;

import cn.bdqn.cart.feign.SkuInfoFeignService;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class SkuInfoFeignFallback implements SkuInfoFeignService {
    @Override
    public R getSkuInfo(Long skuId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public List<String> getSkuAttr(Long skuId) {
        log.warn("远程调用getSkuAttr skudId " + skuId + "失败！已经熔断");
        return null;
    }

    @Override
    public BigDecimal getNewPrice(Long skuId) {
        log.warn("远程调用getNewPrice skudId " + skuId + "失败！已经熔断");
        return null;
    }
}
