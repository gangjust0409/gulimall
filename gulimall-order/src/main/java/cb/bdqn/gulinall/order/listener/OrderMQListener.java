package cb.bdqn.gulinall.order.listener;

import cb.bdqn.gulinall.order.service.OrderService;
import cn.bdqn.gulimall.to.mq.OrderTo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderMQListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void orderListener(OrderTo order, Message message, Channel channel) throws IOException {
        System.out.println("正在关单..");
        try {
            // 修改订单的状态
            orderService.closeOrder(order);
            // 消费信息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //TODO 在进行关单之后进行关闭支付宝支付流程，也可以设置超时时间
            // 关闭支付信息
            //String res = orderService.closeZFB(orderTo.getOrderSn());
            //System.out.println(res);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}
