package cn.bdqn.gulimall.member.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.member.feign.fallback.OrderMemberFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "gulimall-order",fallback = OrderMemberFeignFallback.class)
@Component
public interface OrderMemberFeignService {

    @PostMapping("/order/order/orderWithMember")
    public R queryOrders(@RequestBody Map<String, Object> params);
}
