package com.bdqn.seckill.feign.fallback;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import com.bdqn.seckill.feign.CouponFeignService;
import org.springframework.stereotype.Component;

@Component
public class CouponFeignFallback implements CouponFeignService {
    @Override
    public R getLetes3DaySession() {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
