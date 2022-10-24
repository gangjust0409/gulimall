package cn.bdqn.auth.controller;

import cn.bdqn.auth.feign.MemberFeignService;
import cn.bdqn.auth.feign.ThirdPartServerFeignService;
import cn.bdqn.auth.vo.UserLoginVo;
import cn.bdqn.auth.vo.UserRegisterVo;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.constant.AuthConstant;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.vo.MemberVo;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    // 如果已登录，那么再去登录页直接跳转到登录后的首页
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        // 判断是否登录成功
        Object login = session.getAttribute(AuthConstant.LOGIN_USER);
        if (login != null) {
            return "redirect:http://gulimall.mmf.asia";
        }
        return "login";
    }

    /*

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }*/

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThirdPartServerFeignService thirdPartServerFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 防刷

        // 60 s内同个手机号不能发信息验证码
        String key = redisTemplate.opsForValue().get(AuthConstant.PHONE_CODE_CACHE + phone);
        if (!StringUtils.isEmpty(key)) {
            // 获取redis 存入的时间  System.currentTimeMillis()  以毫秒为单位
            long redis_timeout = Long.parseLong(key.split("_")[1]);
            if(System.currentTimeMillis() - redis_timeout < 60000) {
                System.out.println("System.currentTimeMillis() - redis_timeout："+(System.currentTimeMillis() - redis_timeout));
                return R.error(BizExceptionCode.AUTH_CODE_EXCEPTION.getCode(),BizExceptionCode.AUTH_CODE_EXCEPTION.getMsg());
            }
        }

        // 生成验证码
        String code = UUID.randomUUID().toString().substring(0, 5);

        // 存入redis 中
        redisTemplate.opsForValue().set(AuthConstant.PHONE_CODE_CACHE + phone, code+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);

        thirdPartServerFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * Request method 'POST' not supported
     * 是因为 post请求提交之后，"转发到:/reg.html"，路径映射是都是get访问的，
     * 解决重新提交表单问题 使用重定向
     * RedirectAttributes 实现原理：
     *      内部使用 session 原理。将数据放入session中，下一次页面跳转将数据取出之后，session 数据删掉
     *
     */

    @PostMapping("/regist")
    public String register(@Valid UserRegisterVo vo, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        // 如果验证错误
        if (bindingResult.hasErrors()) {
            // 收集错误信息
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //model.addAttribute("errors", errors);
            // 只需要去一次数据就行了
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.mmf.asia/reg.html";
        }
        // 正常注册
        // 正常注册之前先验证验证码
        String oldCode = vo.getCode();
        // 获取redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(AuthConstant.PHONE_CODE_CACHE + vo.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            // 比较两次验证码
            if (oldCode.equals(redisCode.split("_")[0])) {
                // 删除redis中的验证码
                redisTemplate.delete(AuthConstant.PHONE_CODE_CACHE+vo.getPhone());
                // 调用正确的注册流程
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {


                    return "redirect:http://auth.gulimall.mmf.asia/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg",new TypeReference<String>(){}));
                    //model.addAttribute("errors", errors);
                    // 只需要去一次数据就行了
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.mmf.asia/reg.html";
                }

            } else {
                // 验证码错误
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码不正确");
                //model.addAttribute("errors", errors);
                // 只需要去一次数据就行了
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.mmf.asia/reg.html";
            }
        } else {
            // 验证码错误
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码过期，请重新发送验证码");
            //model.addAttribute("errors", errors);
            // 只需要去一次数据就行了
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.mmf.asia/reg.html";
        }
    }

    // 登录
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            // 登录成功
            // 获取数据
            MemberVo data = r.getData("data", new TypeReference<MemberVo>() {
            });
            session.setAttribute(AuthConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.mmf.asia";
        } else {
            // 登录失败
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.mmf.asia/login.html";
        }
    }

}
