package cn.bdqn.gulimall.search.service.impl;

import cn.bdqn.gulimall.search.config.GulimallElasticSearchConfigura;
import cn.bdqn.gulimall.search.constant.EsConstant;
import cn.bdqn.gulimall.search.service.ProductSearchService;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSerchService implements ProductSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean productSearchUp(List<SkuEsModule> skuEsModules) throws IOException {
        // 批量保存到 es
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModule module : skuEsModules) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(module.getSkuId().toString());
            // 转成 json 数据格式
            String s = JSON.toJSONString(module);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfigura.COMMON_OPTIONS);
        // 如果批量错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());
        log.error("商品上架成功：{}", collect);

        return b;
    }
}
