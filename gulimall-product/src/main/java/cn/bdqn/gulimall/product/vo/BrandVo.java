package cn.bdqn.gulimall.product.vo;

/**
 * 点击发布商品是，根据分类id获取品牌名称和品牌id的vo
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/3
 */
public class BrandVo {

    private Long brandId;
    private String brandName;

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
