package cn.bdqn.auth.controller;

import cn.bdqn.auth.feign.MemberFeignService;
import cn.bdqn.auth.vo.SocialUser;
import cn.bdqn.gulimall.common.utils.HttpUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.constant.AuthConstant;
import cn.bdqn.gulimall.vo.MemberVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理微博的请求
 *
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 1 换取 access_token
        //发送一个请求获取
        // 封装请求体参数
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "3938493381");
        map.put("client_secret", "4b1c4598af34bc6152809295a1e30c72");
        map.put("response_type", "code");
        map.put("redirect_uri", "http://auth.gulimall.mmf.asia/oauth2.0/weibo/success");
        map.put("grant_type", "authorization_code");
        map.put("code", code);

        System.out.println(code);

        HttpResponse res = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", new HashMap<>(), map,new HashMap<>());

        System.out.println(res.getStatusLine().getStatusCode());
        // 处理
        if (res.getStatusLine().getStatusCode() == 200) {
            // 获取响应的数据
            String json = EntityUtils.toString(res.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            // 微博授权登录后，判断是否以前已注册过当前系统
            R r = memberFeignService.oauthLogin(socialUser);
            if (r.getCode() == 0) {
                // 成功登录
                MemberVo data = r.getData("data", new TypeReference<cn.bdqn.gulimall.vo.MemberVo>() {
                });
                // 默认发的令牌  session =fsdfds 作用域：当前作用域  （解决子域 session问题）
                // 使用json序列化格式保存到redis中
                // spring session 内部使用了装饰者模式，封装了request和response，redis 中的session key 过期时间自动续期
                session.setAttribute(AuthConstant.LOGIN_USER, data);

                // 展示信息
                log.info("登录成功！{}"+data);
                // 2 跳转到登录页
                return "redirect:http://gulimall.mmf.asia";
            } else {
                return "redirect:http://auth.gulimall.mmf.asia/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.mmf.asia/login.html";
        }
    }

}
