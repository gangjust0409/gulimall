package cn.bdqn.gulimall.search;

import cn.bdqn.gulimall.search.config.GulimallElasticSearchConfigura;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class GulimallSearchApplicationTests {


    @Resource
    private RestHighLevelClient client;


    @Test
    public void searchData() throws IOException {
        // 定义检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");

        // 指定DSL，索引条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        // 根据年龄的值分布聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        // 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println(searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);

        // 执行检索
        SearchResponse search = client.search(searchRequest, GulimallElasticSearchConfigura.COMMON_OPTIONS);

        // 分析结果
        System.out.println(search);
        SearchHits hits = search.getHits();
        SearchHit[] searchHits = hits.getHits();
        // 将es查询出来命中的数据转成实体类
        for (SearchHit searchHit : searchHits) {
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);
        }

        // 获取聚合里的内容
        Aggregations searchAggregations = search.getAggregations();
//        List<Aggregation> aggregations = searchAggregations.asList(); 可以转成 list 、等
        System.out.println("年龄聚合");
        Terms ageAgg1 = searchAggregations.get("ageAgg");
        List<? extends Terms.Bucket> buckets = ageAgg1.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString());
        }
        System.out.println("平均薪资");
        Avg balanceAvg1 = searchAggregations.get("balanceAvg");
        System.out.println(balanceAvg1.getValue());

    }

    /**
     * 嵌入式属性 nested
     */
    @Data
    @ToString
    static class Account{
        private int account_number;

        private int balance;

        private String firstname;

        private String lastname;

        private int age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

//    添加一条数据 到es
    @Test
    public void indexSave() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.id = "1001";
        user.name = "jack";
        // 转成 json 数据
        String jsonString = JSON.toJSONString(user);
        // 要保存的内容
        indexRequest.source(jsonString, XContentType.JSON);

        // 执行操作
        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfigura.COMMON_OPTIONS);

        System.out.println(index);
    }

    // 查询 es 数据
    @Test
    public void getEs() throws IOException {

    }

    @Data
    class User{
        private String id;
        private String name;
    }

}
