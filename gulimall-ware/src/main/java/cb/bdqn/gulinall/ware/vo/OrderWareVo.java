package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderWareVo {

    // 订单号
    private String orderSn;

    private List<OrderItem> locks;

}
