package cn.bdqn.gulimall.product.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.product.feign.SearchFeignService;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchFeignFallback implements SearchFeignService {
    @Override
    public R productUp(List<SkuEsModule> skuEsModules) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
