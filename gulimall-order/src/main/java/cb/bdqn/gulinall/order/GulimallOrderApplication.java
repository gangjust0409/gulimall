package cb.bdqn.gulinall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 自动配置类 RabbitAutoConfiguration
 * 使用 @RabbitListener（可以标注在方法+类上） 监听注解必须使用@EnableRabbit
 *  发送消息内容的类型可以不一样，可以用以下注解
 *      @RabbitHandler 可以标注在 方法上
 *
 */
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
