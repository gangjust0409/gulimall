package cn.bdqn.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引入依赖
 * 编写配置类  创建一个 RestHighLevelClient
 *
 */
@Configuration
public class GulimallElasticSearchConfigura {

    // 设置请求选项
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient restTemplate() {
        // final String hostname, final int port, final String scheme
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.85.130", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

}
