package cn.bdqn.cart.feign;

import cn.bdqn.cart.feign.fallback.SkuInfoFeignFallback;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "gulimall-product",fallback = SkuInfoFeignFallback.class)
@Component
public interface SkuInfoFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/sku/attr/{skuId}")
    List<String> getSkuAttr(@PathVariable Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/getPrice")
    BigDecimal getNewPrice(@PathVariable Long skuId);

}
