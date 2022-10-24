package cb.bdqn.gulinall.ware.feign;

import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
@Component
public interface OrderFeignService {

    @GetMapping("/order/order/order/state/{orderSn}")
    R orderByOrderSn(@PathVariable String orderSn);
}
