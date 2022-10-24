package cb.bdqn.gulinall.order.feign.fallback;

import cb.bdqn.gulinall.order.feign.MemberAddressFeignService;
import cb.bdqn.gulinall.order.vo.MemberAddressVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MemberAddressFeignFallback implements MemberAddressFeignService {
    @Override
    public List<MemberAddressVo> addressEntities(Long memberid) {
        log.warn("远程调用MemberAddressFeignFallback "+memberid+" addressEntities 失败...");
        return null;
    }
}
