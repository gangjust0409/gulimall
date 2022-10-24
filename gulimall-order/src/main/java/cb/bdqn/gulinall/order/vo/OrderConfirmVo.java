package cb.bdqn.gulinall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 结算页渲染数据
 */
public class OrderConfirmVo {

    // 该会员的地址信息
    @Getter @Setter
    private List<MemberAddressVo> address;

    // 购物车的商品信息
    @Getter @Setter
    private List<OrderItem> items;

    // 优惠卷  积分
    @Getter @Setter
    private Integer useIntegration;

    // 是否有库存
    @Getter @Setter
    private Map<Long, Boolean> stocks;

    // 商品总额
    //private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal("0");
        if (items != null) {
            for (OrderItem item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                totalPrice = totalPrice.add(multiply);
            }
        }
        return totalPrice;
    }

    // 应付总额
    //private BigDecimal payPrice;

    // 因为没有啥优惠信息，所以直接调用所有价格
    public BigDecimal getPayPrice() {
        return this.getTotalPrice();
    }

    public Integer getCount(){
        int count = 0;
        if (items != null) {
            for (OrderItem item : items) {
                ++count;
            }
        }
        return count;
    }

    // 放重令牌
    @Getter @Setter
    private String orderToken;

}
