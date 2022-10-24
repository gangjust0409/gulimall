package cn.bdqn.auth.feign.fallback;

import cn.bdqn.auth.feign.ThirdPartServerFeignService;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartServerFallback implements ThirdPartServerFeignService {
    @Override
    public R sendCode(String phone, String code) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
