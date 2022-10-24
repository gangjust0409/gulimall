package cb.bdqn.gulinall.order.feign.fallback;

import cb.bdqn.gulinall.order.feign.ProductFeignService;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignFallback implements ProductFeignService {
    @Override
    public R spuInfoBySkuId(Long skuId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
