package cb.bdqn.gulinall.order.web;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send/order")
    @ResponseBody
    public String searchYanShiMessage(){
        OrderEntity order = new OrderEntity();
        order.setOrderSn(IdWorker.getTimeId());
        order.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order);
        return "ok";
    }

    @GetMapping("/{page}.html")
    public String toPage(@PathVariable String page){

        return page;
    }

}
