package cb.bdqn.gulinall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * spring 可以使用 @Bean 创建 交换机 、 队列 、 绑定关系
 */
@Configuration
public class MyMQConfig {

    /*@RabbitListener(queues = {"order.release.order.queue"})
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("order " + order);
        // 手动查看消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }*/

    // 创建交换机  两个队列关系一个交换机 需要使用topic交换机
    // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true,false,null);
    }

    // 两个队列

    /**
     * 死信队列
     * x-dead-letter-exchange: order-event-exchange  死信路由
     * x-dead-letter-routing-key: order.release.order  死信的route-key
     * x-message-ttl: 60000   消息的存活时间
     * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl", 120000);

        return new Queue("order.delay.queue",true,false,false, arguments);
    }

    /**
     * 普通队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue(){

        return new Queue("order.release.order.queue",true,false,false);
    }

    // 绑定关系

    /**
     * String destination, DestinationType destinationType, String exchange, String routingKey,
     * 			Map<String, Object> arguments
     * @return
     */
    @Bean
    public Binding orderCreateOrder(){
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrder(){
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.order", null);
    }

    /**
     * 当订单信息卡顿，
     */
    @Bean
    public Binding orderReleaseOther(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.order.#", null);
    }

    //创建一个队列做销峰处理
    @Bean
    public Queue orderSeckillOrderQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments

        return new Queue("order.seckill.order.queue",true,false,false);
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding(){
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey,
         * 			Map<String, Object> arguments
         */
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange","order.seckill.order",null);
    }

}
