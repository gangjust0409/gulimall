package cb.bdqn.gulinall.order.feign;

import cb.bdqn.gulinall.order.feign.fallback.WareFeignFallback;
import cb.bdqn.gulinall.order.vo.OrderWareVo;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "gulimall-ware",fallback = WareFeignFallback.class)
@Component
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R wareSku(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/free")
    R jisuanFree(@RequestParam("attrId") Long attrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLock(@RequestBody OrderWareVo vo);
}
