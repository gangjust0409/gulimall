package cn.bdqn.gulimall.search.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.search.feign.fallback.ProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "gulimall-product", fallback = ProductFeignFallback.class)
public interface ProductFeignService {

    @RequestMapping("/product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);


    @GetMapping("/product/brand/infos")
    public R brandIds(@RequestParam("brandId") List<Long> brandId);

}
