package cn.bdqn.cart.vo;

import org.just.utils.JavaUtils;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    // 条数
    private List<CartItem> items;
    private Integer countNum; // 商品数量
    private Integer countType; // 选中商品件数
    private BigDecimal sumPrice; // 总价
    private BigDecimal promotion = new BigDecimal("0"); // 促销价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        this.countNum = 0;
        if (JavaUtils.listIsNull(items)){
            for (CartItem item : items) {
                this.countNum++;
            }
        }
        return countNum;
    }


    public Integer getCountType() {
        this.countType = 0;
        if (JavaUtils.listIsNull(items)) {
            for (CartItem item : items) {
                if (item.getChecked()) {
                    this.countType += item.getCount();
                }
            }
        }
        return countType;
    }


    public BigDecimal getSumPrice() {
        sumPrice = new BigDecimal("0");
        if (JavaUtils.listIsNull(items)) {
            for (CartItem item : items) {
                if (item.getChecked()) {
                    System.out.println(item);
                    BigDecimal totalPrice = item.getTotalPrice();
                    sumPrice = sumPrice.add(totalPrice);
                }
            }
        }
        // 减去优惠
        sumPrice = sumPrice.subtract(this.getPromotion());

        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }

    public BigDecimal getPromotion() {
        return promotion;
    }

    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }
}
