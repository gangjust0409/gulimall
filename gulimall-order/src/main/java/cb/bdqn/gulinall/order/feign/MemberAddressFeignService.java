package cb.bdqn.gulinall.order.feign;

import cb.bdqn.gulinall.order.feign.fallback.MemberAddressFeignFallback;
import cb.bdqn.gulinall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "gulimall-member",fallback = MemberAddressFeignFallback.class)
@Component
public interface MemberAddressFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberid}/address")
    List<MemberAddressVo> addressEntities(@PathVariable Long memberid);

}
