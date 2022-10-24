package cn.bdqn.gulimall.product.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.product.feign.CouponFeignService;
import cn.bdqn.gulimall.to.SkuCouponTo;
import cn.bdqn.gulimall.to.SkuReductioinTo;
import org.springframework.stereotype.Component;

@Component
public class CouponFeignFallback implements CouponFeignService {
    @Override
    public R saveBounds(SkuCouponTo skuCouponTo) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R saveSkuRecution(SkuReductioinTo skuReductioinTo) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
