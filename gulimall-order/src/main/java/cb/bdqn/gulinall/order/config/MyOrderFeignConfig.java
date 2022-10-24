package cb.bdqn.gulinall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MyOrderFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        /**
         * this.target.apply(template);
         */
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
               // 通过容器上下文获取原来的头部信息
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request != null){
                        String header = request.getHeader("Cookie");
                        requestTemplate.header("Cookie", header);
                    }
                }
            }
        };
    }

}
