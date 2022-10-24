package cb.bdqn.gulinall.order.feign.fallback;

import cb.bdqn.gulinall.order.feign.CartFeignService;
import cb.bdqn.gulinall.order.vo.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CartFeignFallback implements CartFeignService {
    @Override
    public List<OrderItem> getCurrentCartItems() {
        log.warn("远程调用CartFeignService getCurrentCartItems 失败...");
        return null;
    }
}
