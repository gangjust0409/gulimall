package cb.bdqn.gulinall.order.controller;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class RabbitmqController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMsg(@RequestParam("num") Integer num){
        for (int i = 0; i < num; i++) {
            /*OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
            entity.setCreateTime(new Date());
            entity.setId(1L);
            entity.setName("OrderReturnReasonEntity  i => " + i);
            //
            rabbitTemplate.convertAndSend("java-exchange","hello.java",entity);*/
            if (i %2 == 0) {
                OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
                entity.setCreateTime(new Date());
                entity.setId(1L);
                entity.setName("OrderReturnReasonEntity  i => " + i);
                //
                rabbitTemplate.convertAndSend("java-exchange","hello.java",entity, new CorrelationData(UUID.randomUUID().toString()));
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setReceiverName("order i => " + i);
                rabbitTemplate.convertAndSend("java-exchange","hello.java",orderEntity, new CorrelationData(UUID.randomUUID().toString()));
            }
        }

        log.info("发送消息成功！");
        return "ok";
    }

}
