package cn.bdqn.gulimall.member.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.member.feign.OrderMemberFeignService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderMemberFeignFallback implements OrderMemberFeignService {
    @Override
    public R queryOrders(Map<String, Object> params) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
