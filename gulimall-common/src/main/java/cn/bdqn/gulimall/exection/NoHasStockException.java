package cn.bdqn.gulimall.exection;

public class NoHasStockException extends RuntimeException {
    private Long skuId;



    public NoHasStockException() {
        super("商品没有库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
