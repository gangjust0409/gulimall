package cn.bdqn.auth.feign;

import cn.bdqn.auth.feign.fallback.MemberFallback;
import cn.bdqn.auth.vo.SocialUser;
import cn.bdqn.auth.vo.UserLoginVo;
import cn.bdqn.auth.vo.UserRegisterVo;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "gulimall-member",fallback = MemberFallback.class)
@Component
public interface MemberFeignService {


    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/auth/login")
    R oauthLogin(@RequestBody SocialUser user) throws Exception;

}
