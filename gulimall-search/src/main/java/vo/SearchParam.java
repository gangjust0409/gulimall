package vo;

import lombok.Data;

import java.util.List;

/**
 * 封装商品检索条件
 *      catelogId=1&keyword=fdsaf&
 */
@Data
public class SearchParam {
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort=salaCount_asc/desc
     * sort=skuPrice/desc
     * sort=hotScope/desc
     */
    private String sort;

    /**
     * 过滤条件
     * hasStock （是否有货）0/1
     * skuPrice=0_500/_500/500_
     * brandId=1
     * attrs=2_5寸:6寸  多个attr属性以:分割
     *
     */
    /**
     * 是否有货  1 有货， 0 无货，默认 1
     */
    private Integer hasStock = 1;
    /**
     * 价格区间查询
     */
    private String skuPrice;

    /**
     * 按照品牌id查询，可以多选
     */
    private List<Long> brandId;
    /**
     * 按照属性进行查询，多选
     */
    private List<String> attrs;
    /**
     * 页码
     */
    private Integer pageNum = 1;

    // url地址后面的参数
    private String urlQuery;

}
