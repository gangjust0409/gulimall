package cn.bdqn.gulimall.product.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.product.feign.fallback.WareSkuFeignFallback;
import cn.bdqn.gulimall.vo.SkuStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "gulimall-ware",fallback = WareSkuFeignFallback.class)
@Component
public interface WareSkuFeignService {

    @PostMapping("/ware/waresku/hasstock")
    public R wareSku(@RequestBody List<Long> skuIds);

}
