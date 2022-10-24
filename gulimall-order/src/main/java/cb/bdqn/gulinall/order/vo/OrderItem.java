package cb.bdqn.gulinall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItem {
    private Long skuId;
    private List<String> skuAttr;
    private String title;
    private String defaultImg;
    private BigDecimal price; // 价格
    private Integer count; // 当前商品的数量
    private BigDecimal totalPrice; // 当前商品的小计
    private int weight; // 商品重量
}
