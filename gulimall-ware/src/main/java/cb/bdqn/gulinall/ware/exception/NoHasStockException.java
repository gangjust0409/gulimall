package cb.bdqn.gulinall.ware.exception;

public class NoHasStockException extends RuntimeException {
    private Long skuId;

    public NoHasStockException(Long skuId) {
        super("商品id： "+skuId+"没有库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
