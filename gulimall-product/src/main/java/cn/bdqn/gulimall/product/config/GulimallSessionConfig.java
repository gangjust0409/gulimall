package cn.bdqn.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class GulimallSessionConfig {

    // 自定义 spring session
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        // 设置 父域名可以访问子域名设置
        defaultCookieSerializer.setDomainName("gulimall.mmf.asia");
        // 设置 cookie 名字
        defaultCookieSerializer.setCookieName("GULIMALLSESSION");

        return defaultCookieSerializer;
    }

    // 保存 redis 为 json串
    @Bean
    public RedisSerializer redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
