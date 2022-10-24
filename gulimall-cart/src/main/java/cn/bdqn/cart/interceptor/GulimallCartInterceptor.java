package cn.bdqn.cart.interceptor;

import cn.bdqn.cart.to.UserInfoTo;
import cn.bdqn.gulimall.constant.AuthConstant;
import cn.bdqn.gulimall.constant.CartConstant;
import cn.bdqn.gulimall.vo.MemberVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class GulimallCartInterceptor implements HandlerInterceptor {

    /**
     * 线程共享，
     */
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberVo memberVo = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER);
        // 判断是否登录
        if (memberVo!=null){
            // 已登录
            userInfoTo.setUserId(memberVo.getId());
        }
        // 没有登录
        // 判断浏览器是否带有临时用户的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.CART_COOKIE_NAME)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setIsTemp(true);
                }
            }
        }
        // 当是浏览器没有user-key 才分配
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        // 如果没有带user-key 那就分配一个临时身份给
        if (!userInfoTo.getIsTemp()) {

            Cookie cookie = new Cookie(CartConstant.CART_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.mmf.asia"); // 设置cookie作用域
            cookie.setMaxAge(CartConstant.CART_COOKIE_TIMEOUT);

            response.addCookie(cookie);
        }
    }
}
