package vo;

import cn.bdqn.gulimall.to.es.SkuEsModule;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {

    // 查询到所有的商品信息
    private List<SkuEsModule> products;

    // 以下是分页信息
    private Integer pageNum; // 当前页
    private Integer totalPages; // 总页码
    private Long total;  // 总条数
    private List<Integer> pageNavs; // 显示的页的个数

    // 查询的品牌集合
    private List<BrandVo> brands;

    // 查询的分类集合
    private List<CatalogVo> catalogs;

    // 查询的属性集合
    private List<AttrVo> attrs;

    // 面包屑
    private List<NavVo> navs = new ArrayList<>();

    // 属性 id 集合
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link; // 当把当前的面包屑删除之后跳转到哪

    }


    // 封装品牌 vo
    @Data
    public static class BrandVo{

        private Long brandId;

        private String brandName;

        private String brandImg;
    }

    @Data
    public static class CatalogVo{

        private Long catalogId;

        private String catalogName;
    }


    @Data
    public static class AttrVo{

        private Long attrId;

        // 属性名
        private String attrName;

        // 多个属性值
        private List<String> attrValue;
    }


}
