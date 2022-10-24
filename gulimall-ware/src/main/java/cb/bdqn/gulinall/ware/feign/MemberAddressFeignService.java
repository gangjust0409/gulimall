package cb.bdqn.gulinall.ware.feign;

import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
@Component
public interface MemberAddressFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R attrInfo(@PathVariable("id") Long id);
}
