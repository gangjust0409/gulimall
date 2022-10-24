package cn.bdqn.gulimall.product.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.product.feign.WareSkuFeignService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WareSkuFeignFallback implements WareSkuFeignService {
    @Override
    public R wareSku(List<Long> skuIds) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
