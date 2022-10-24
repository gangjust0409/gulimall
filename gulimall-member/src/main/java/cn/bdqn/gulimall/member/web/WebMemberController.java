package cn.bdqn.gulimall.member.web;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.member.feign.OrderMemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebMemberController {

    @Autowired
    OrderMemberFeignService orderMemberFeignService;

    @GetMapping("/member-list.html")
    public String toMemberPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               Model model){
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());

        R r = orderMemberFeignService.queryOrders(params);
        model.addAttribute("order", r);
        System.out.println(r);
        return "orderList";
    }

}
