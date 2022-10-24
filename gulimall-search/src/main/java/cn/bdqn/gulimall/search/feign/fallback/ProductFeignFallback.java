package cn.bdqn.gulimall.search.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.search.feign.ProductFeignService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductFeignFallback implements ProductFeignService {
    @Override
    public R attrInfo(Long attrId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R brandIds(List<Long> brandId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
