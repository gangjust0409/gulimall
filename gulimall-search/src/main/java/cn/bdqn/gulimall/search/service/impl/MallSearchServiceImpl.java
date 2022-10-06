package cn.bdqn.gulimall.search.service.impl;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.search.config.GulimallElasticSearchConfigura;
import cn.bdqn.gulimall.search.constant.EsConstant;
import cn.bdqn.gulimall.search.feign.ProductFeignService;
import cn.bdqn.gulimall.search.service.MallSearchService;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.util.UriEncoder;
import vo.AttrResponseVo;
import vo.SearchParam;
import vo.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 查询es
     * @param param
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {

        SearchResult searchResult = null;

        //1 定义检索请求
        SearchRequest searchRequest = builderSearchRequest(param);

        try {
            // 2 执行
            SearchResponse search = client.search(searchRequest, GulimallElasticSearchConfigura.COMMON_OPTIONS);

            // 分析响应结果
            searchResult = builderSearchResult(search, param);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return searchResult;
    }

    /**
     * 定义检索请求
     * @param param
     * @return
     */
    private SearchRequest builderSearchRequest(SearchParam param) {
        // 构建 dsl 语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // query - bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // must
        if (!StringUtils.isEmpty(param.getKeyword()))
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));

        // filter  分类id
        if (param.getCatalog3Id() != null)
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));

        // filter 品牌id
        if (!CollectionUtils.isEmpty(param.getBrandId()) && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }

        // filter 属性   每个对象下的的对象必须用nested
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attrStr : param.getAttrs()) {
                // 每个都得生成 nested
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                // 地址是以这种方式  attrs=1_1.5寸:8寸
                String[] s = attrStr.split("_");
                // 属性id
                String attrId = s[0];
                // 属性值
                String[] attrValues = s[1].split(":");
                // nested 属性id  nested termQuery  里的属性 必须和es中的字段一样 ，不然导致数据查不出
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                // nested 属性值
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                // ScoreMode.none（不参与） 是否参与评分
                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(attrs);
            }
        }
       /* if(param.getAttrs() != null && param.getAttrs().size() > 0){

            param.getAttrs().forEach(item -> {
                //attrs=1_5寸:8寸&2_16G:8G
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


                //attrs=1_5寸:8寸
                String[] s = item.split("_");
                String attrId=s[0];
                String[] attrValues = s[1].split(":");//这个属性检索用的值
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            });

        }*/

        // filter 是否有库存
        //TODO 暂时把 库存设置为0，好查询
        boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));

        // filter 价格区间  0_600 | _500 | 10_
        if (!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = param.getSkuPrice().split("_");
            // 正常区间
            if (prices.length == 2) {
                skuPrice.gte(prices[0]).lte(prices[1]);
            } else if(prices.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    skuPrice.lte(prices[0]);
                } else if(param.getSkuPrice().endsWith("_")){
                    skuPrice.gte(prices[0]);
                }
            }

            boolQuery.filter(skuPrice);
        }

        // query 匹配到的查询条件
        sourceBuilder.query(boolQuery);

        // 排序，分页、高亮
        // 排序 sort=hotScope/desc
        // 获取字段和排序方式
        if (!StringUtils.isEmpty(param.getSort())) {
            String[] s = param.getSort().split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            //System.out.println("排序方式：" + sortOrder); 排错问题（不回升序排列）：不能用 == ， s[1].equalsIgnoreCase("asc")
            sourceBuilder.sort(s[0],sortOrder);
        }

        // 分页  (pagenum-1)*pagesize
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red;'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 聚合
        // 品牌
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        // 品牌子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        // 分类
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);
        // 分类子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);
        // 属性
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrs_agg", "attrs");
        // 属性子聚合
        TermsAggregationBuilder attrId_agg = AggregationBuilders.terms("attrId_agg");
        attrId_agg.field("attrs.attrId").size(50);
        // 每个id的子聚合
        attrId_agg.subAggregation(AggregationBuilders.terms("attr_name_Agg").field("attrs.attrName").size(1));
        attrId_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested.subAggregation(attrId_agg);
        sourceBuilder.aggregation(nested);

        System.out.println("DSL语句：" + sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * 分析响应结果
     * @param search
     * @return
     */
    private SearchResult builderSearchResult(SearchResponse search, SearchParam param) {
        List<SkuEsModule> esModules = new ArrayList<>();
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();

        SearchResult result = new SearchResult();
        SearchHits hits = search.getHits();
        // 所有商品信息
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            SkuEsModule esModule = JSON.parseObject(sourceAsString, SkuEsModule.class);
            // 查询到的商品信息的名字需要高亮显示
            if (!StringUtils.isEmpty(param.getKeyword())){
                // 获取高亮
                String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].string();
                esModule.setSkuTitle(skuTitle);
            }
            esModules.add(esModule);
        }
        // 设置商品信息
        result.setProducts(esModules);

        Aggregations aggregations = search.getAggregations();
        // 当前商品的品牌信息
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            // 品牌id
            Long brandId = Long.parseLong(bucket.getKeyAsString());
            // 品牌名称
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            // 品牌图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            // 封装 vo
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            // 添加到集合
            brandVos.add(brandVo);
        }
        // 设置品牌信息
        result.setBrands(brandVos);

        // 当前商品的分类信息
        ParsedLongTerms catalogAgg = aggregations.get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            // 分类id
            Long catalogId = Long.parseLong(bucket.getKeyAsString());
            // 分类名称
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            // 封装 vo
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(catalogId);
            catalogVo.setCatalogName(catalogName);
            // 添加到集合
            catalogVos.add(catalogVo);
        }
        // 设置分类信息
        result.setCatalogs(catalogVos);

        // 当前商品的属性信息
        ParsedNested attrsAgg = aggregations.get("attrs_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrId_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            // 属性id
            Long attrId = Long.parseLong(bucket.getKeyAsString());
            // 属性名称
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_Agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 属性值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> ((Terms.Bucket) item).getKeyAsString()).collect(Collectors.toList());
            // 封装 vo
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            // 添加到集合
            attrVos.add(attrVo);
        }
        // 设置属性信息
        result.setAttrs(attrVos);

        // 分页信息
        // 当前页
        result.setPageNum(param.getPageNum());
        // 总条数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        // 总页码  公式：总条数/每页显示数 （如果有余数，说明还有 (总条数/每页显示数)+1）
        Integer totalPages = (int)total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int)total / EsConstant.PRODUCT_PAGESIZE : (int)(total / EsConstant.PRODUCT_PAGESIZE) + 1;
        result.setTotalPages(totalPages);

        // 页的个数
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        // 设置页的个数
        result.setPageNavs(pageNavs);

        // 6 面包屑
       if (param.getAttrs() != null && param.getAttrs().size() > 0) {
           List<SearchResult.NavVo> navs = param.getAttrs().stream().map(attr -> {
               String[] attrs = attr.split("_");
               SearchResult.NavVo navVo = new SearchResult.NavVo();
               navVo.setNavValue(attrs[1]);
               // 远程查询属性的名字
               R r = productFeignService.attrInfo(Long.parseLong(attrs[0]));

               // 设置 attr id
               result.getAttrIds().add(Long.parseLong(attrs[0]));
               // 远程调用是否成功
               if (r.getCode() == 0) {
                   AttrResponseVo attr1 = r.getData("attr", new TypeReference<AttrResponseVo>() {});
                   // 设置属性名称
                   navVo.setNavName(attr1.getAttrName());
                   // 设置删除后跳转到哪
                   // 替换路径当前属性  attrs=15_海思（Hisilicon）
                   String replace = rangeQueryString(param, attr, "attrs");
                   navVo.setLink("http://search.gulimall.mmf.asia/list.html?" + replace);
               } else {
                   navVo.setNavName(attrs[0]);
               }
               return navVo;
           }).collect(Collectors.toList());
           result.setNavs(navs);

       }
        // 品牌
        if (param.getBrandId() != null && param.getBrandId().size()>0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            // 查询品牌名称
            R r = productFeignService.brandIds(param.getBrandId());
            StringBuffer sb = new StringBuffer();
            if (r.getCode() == 0) {
                List<SearchResult.BrandVo> brands = r.getData("brand", new TypeReference<List<SearchResult.BrandVo>>() {
                });
                String replace = "";
                for (SearchResult.BrandVo brand : brands) {
                    sb.append(brand.getBrandName() + "；");
                    replace  = rangeQueryString(param, brand.getBrandId().toString(), "brandId");
                }
                navVo.setNavValue(sb.toString());
                navVo.setLink("http://search.gulimall.mmf.asia/list.html?" + replace);
            }
            // 设置 删除后跳转到的路径

            navs.add(navVo);
        }

        return result;
    }

    private String rangeQueryString(SearchParam param, String attr,String key) {
        // url 地址乱码，需要用 UriEncoder.encode(attr);转换
        String decode = UriEncoder.encode(attr);
        // 将url地址里的java语言的空格转换前端的空格
        decode = decode.replace("+","%20");
        //TODO 这里需要扩展  当是第一个查询参数时，不能删除
        return param.getUrlQuery().replace("&"+key+"=" + decode, "");
    }
}
