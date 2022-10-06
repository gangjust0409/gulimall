package cn.bdqn.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayCorsConfig {

    @Bean
    public CorsWebFilter webFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();
        // 配置跨域
        configuration.addAllowedHeader("*"); // 允许哪些请求头进行跨域
        configuration.addAllowedMethod("*"); // 允许哪些请求方式跨域
        configuration.addAllowedOrigin("*");
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }

}
