package cn.bdqn.gulimall.thirdparty.controller;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/sms")
@RestController
public class SmsController {

    @Autowired
    private SmsComponent smsComponent;

    /**
     * 供其他服务发送短信验证码
     * @return
     */
    @GetMapping("/third/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsComponent.sendCode(phone, code);
        return R.ok();
    }

}
