package cn.bdqn.auth.feign.fallback;

import cn.bdqn.auth.feign.MemberFeignService;
import cn.bdqn.auth.vo.SocialUser;
import cn.bdqn.auth.vo.UserLoginVo;
import cn.bdqn.auth.vo.UserRegisterVo;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import org.springframework.stereotype.Component;

@Component
public class MemberFallback implements MemberFeignService {
    @Override
    public R regist(UserRegisterVo vo) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R login(UserLoginVo vo) {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }

    @Override
    public R oauthLogin(SocialUser user) throws Exception {
        return R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(),BizExceptionCode.NO_MANY_REQUEST.getMsg());
    }
}
