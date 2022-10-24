package cb.bdqn.gulinall.order.feign;

import cb.bdqn.gulinall.order.feign.fallback.ProductFeignFallback;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimall-product",fallback = ProductFeignFallback.class)
@Component
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/spuInfo/{id}")
    R spuInfoBySkuId(@PathVariable("id") Long skuId);
}
