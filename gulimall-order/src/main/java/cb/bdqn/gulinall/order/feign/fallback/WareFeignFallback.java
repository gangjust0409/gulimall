package cb.bdqn.gulinall.order.feign.fallback;

import cb.bdqn.gulinall.order.feign.WareFeignService;
import cb.bdqn.gulinall.order.vo.OrderWareVo;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WareFeignFallback implements WareFeignService {
    @Override
    public R wareSku(List<Long> skuIds) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R jisuanFree(Long attrId) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R orderLock(OrderWareVo vo) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
