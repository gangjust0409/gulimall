package cb.bdqn.gulinall.order.to;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice; // 应付价格

    private BigDecimal fare; // 运费

}
