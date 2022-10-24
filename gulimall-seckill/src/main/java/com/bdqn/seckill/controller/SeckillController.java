package com.bdqn.seckill.controller;

import cn.bdqn.gulimall.common.utils.R;
import com.bdqn.seckill.service.SeckillService;
import com.bdqn.seckill.to.SeckillSKuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    @ResponseBody
    @GetMapping("/currentSeckillSkuInfo")
    public R getCurrentSeckillSkuInfo(){
        log.info("调用了一次方法getCurrentSeckillSkuInfo");
        List<SeckillSKuRedisTo> tos = seckillService.getCurrentSeckillSkuInfo();

        return R.ok().setData(tos);
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSeckillSkuInfo(@PathVariable Long skuId){
        SeckillSKuRedisTo to = seckillService.getSeckillSkuInfo(skuId);
        return R.ok().setData(to);
    }

    //http://seckill.gulimall.mmf.asia/kill?killId=3_55&key=6f6caaa861f4417c9a337d9535c5b7dd&num=1
    @GetMapping("/kill")
    public String kill(@RequestParam("killId") String killId,
                       @RequestParam("key") String key,
                       @RequestParam("num") String num, Model model){
        // 必须先登录.只要秒杀成功，那么就返回一个订单号发给给mq

        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }

}
