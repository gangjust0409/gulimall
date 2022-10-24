package cb.bdqn.gulinall.order;


import cb.bdqn.gulinall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建 exchange、queue、binding
     *  发送和接受消息
     */
    @Test
    public void createExchange() {
        // 创建交换机 String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        DirectExchange directExchange = new DirectExchange("java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("创建{}交换机成功！","java-exchange");

    }

    // 创建队列 queue
    @Test
    public void createQueue(){
        // String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("创建{}队列成功！","java-queue");
    }

    // 创建binding
    @Test
    public void createBinding(){
        // String destination【目的地 队列名称】
        // , DestinationType destinationType,【队列类型】
        // String exchange,【交换机名称】
        // String routingKey,【route-key】
        //			Map<String, Object> arguments【参数】
        Binding binding = new Binding("java-queue", Binding.DestinationType.QUEUE,
                "java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("创建{}绑定成功！","java-queue");
    }

    @Test
    public void sendMsg(){
        String msg = "hello word";
        // 发送的消息是对象  那么 会使用 Serializable 序列化，想使用json格式,MessageConverter
        // 默认使用的是 WhiteListDeserializingMessageConverter 序列化
        OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
        entity.setCreateTime(new Date());
        entity.setId(1L);
        entity.setName("哈哈");
        //
        rabbitTemplate.convertAndSend("java-exchange","hello.java",entity);
        log.info("发送消息{}成功！",entity);


    }

}
