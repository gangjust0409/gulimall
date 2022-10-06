package cn.bdqn.gulimall.product.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
@Component
public interface SearchFeignService {

    @PostMapping("/es/save/product")
    public R productUp(@RequestBody List<SkuEsModule> skuEsModules);

}
