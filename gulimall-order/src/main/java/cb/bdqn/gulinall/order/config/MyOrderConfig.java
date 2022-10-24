package cb.bdqn.gulinall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyOrderConfig {


    RabbitTemplate rabbitTemplate;

    @Primary // 主要的配置
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        this.rabbitTemplate=rabbitTemplate;
        initRabbitTemplate();
        return rabbitTemplate;
    }

    // 使用消息内容格式使用 json 格式  默认使用注入的容器优先
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 设置服务端确认
    //@PostConstruct // MyOrderConfig调用构造器时调用
    public void initRabbitTemplate(){
        // 生产者到消费者
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 需要开启 spring.rabbitmq.publisher-confirms=true
             *
             * new CorrelationData(UUID.randomUUID().toString()) 设置这里的id
             * 发送端确认
             * @param correlationData 当前消息的唯一关联id（唯一id）
             * @param ack 消息是否成功送达
             * @param s 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String s) {
                System.out.println("correlationData：" + correlationData + "，b：" + ack+"，s：" + s);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 需要开启以下
             * spring.rabbitmq.publisher-returns=true
             * spring.rabbitmq.template.mandatory=true（这个可以不用开启，默认开启）
             * @param message 哪个消息投递失败了
             * @param i 回复的状态码
             * @param s 回复的文本
             * @param s1 交换机
             * @param s2 route-key
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                //TODO 当服务发送消息gei服务代理时，消息丢失，所以，这里需要记录保存日志修改消息的状态，定期扫描数据库进行重试发送
                System.out.println("message：" + message + "，i："+i+"s：" + s+"s1："+s1+"s2："+s2);

            }
        });

    }


}
