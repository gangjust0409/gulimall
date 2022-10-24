package cb.bdqn.gulinall.order.feign;

import cb.bdqn.gulinall.order.feign.fallback.CartFeignFallback;
import cb.bdqn.gulinall.order.vo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "gulimall-cart",fallback = CartFeignFallback.class)
@Component
public interface CartFeignService {


    @GetMapping("/getCurrentItems")
    List<OrderItem> getCurrentCartItems();

}
