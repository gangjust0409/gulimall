package cb.bdqn.gulinall.order.config;

import cb.bdqn.gulinall.order.interceptor.OrderLoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrderWebConfig implements WebMvcConfigurer {

    @Autowired
    OrderLoginUserInterceptor orderLoginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderLoginUserInterceptor).addPathPatterns("/**");
    }
}
