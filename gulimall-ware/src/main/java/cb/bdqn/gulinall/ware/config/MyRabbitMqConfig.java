package cb.bdqn.gulinall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitMqConfig {

   /* @Autowired
    RabbitTemplate rabbitTemplate;*/

    // 使用消息内容格式使用 json 格式  默认使用注入的容器优先
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /*@RabbitListener(queues = {"stock.release.stock.queue"})
    public void create(){}
*/
    // 创建交换机
    @Bean
    public Exchange stockEventExchange(){
        // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("stock-event-exchange",true,false);
    }

    // 创建接收死信的队列   普通队列
    // String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    // 创建死信队列

    /**
     * x-dead-letter-exchange: stock-event-exchange  死信路由转到的交换机
     * x-dead-letter-routing-key: order.release.order  死信的route-key
     * x-message-ttl: 60000   消息的存活时间
     *  String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "stock-event-exchange");
        args.put("x-dead-letter-routing-key", "stock.release");
        args.put("x-message-ttl", 120000);

        return new Queue("stock.delay.queue",true,false,false,args);
    }

    // 绑定关系

    /**
     * String destination, DestinationType destinationType, String exchange, String routingKey,
     * 			Map<String, Object> arguments
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.release.#", null);
    }

    /**
     * String destination, DestinationType destinationType, String exchange, String routingKey,
     * 			Map<String, Object> arguments
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.locked", null);
    }

}
