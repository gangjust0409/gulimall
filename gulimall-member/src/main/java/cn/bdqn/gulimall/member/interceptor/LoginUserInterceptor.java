package cn.bdqn.gulimall.member.interceptor;

import cn.bdqn.gulimall.constant.AuthConstant;
import cn.bdqn.gulimall.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理这个死循环处理
        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", requestURI);
        if (match) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberVo memberVo = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if(memberVo!=null){
            // 已登录
           threadLocal.set(memberVo);
            return true;
        } else {
            // 没有登录
            session.setAttribute("msg", "抱歉，您没有登录，请先登录！");
            response.sendRedirect("http://auth.gulimall.mmf.asia/login.html");
            return false;
        }
    }
}
