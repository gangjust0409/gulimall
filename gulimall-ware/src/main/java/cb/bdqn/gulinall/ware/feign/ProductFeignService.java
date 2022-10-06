package cb.bdqn.gulinall.ware.feign;

import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/5
 */
@FeignClient("gulimall-gateway")
@Component
public interface ProductFeignService {

    /**
     * feign可以通过网关调用其他服务
     *  因为网关需要用api做前置路径，所以需要添加
     *
     */
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
