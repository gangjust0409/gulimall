package cb.bdqn.gulinall.order.listener;

import cb.bdqn.gulinall.order.service.OrderService;
import cn.bdqn.gulimall.to.mq.SeckillOrderTo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class SeckillOrderListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void handlerSeckillOrder(SeckillOrderTo to, Channel channel, Message message) throws IOException {
        try {
            orderService.quickOrder(to);
            //消费消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
        }
    }

}
