package cb.bdqn.gulinall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/6
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor page() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        //溢出总页数后是否进行处理
        paginationInnerInterceptor.setOverflow(true);
        //单页分页条数限制(默认无限制
        paginationInnerInterceptor.setMaxLimit(1000L);
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);

        return mybatisPlusInterceptor;

    }
}
