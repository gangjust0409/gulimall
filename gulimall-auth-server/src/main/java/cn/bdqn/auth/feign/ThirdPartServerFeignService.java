package cn.bdqn.auth.feign;

import cn.bdqn.auth.feign.fallback.ThirdPartServerFallback;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "gulimall-third-paty",fallback = ThirdPartServerFallback.class)
@Component
public interface ThirdPartServerFeignService {

    @GetMapping("/sms/third/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
