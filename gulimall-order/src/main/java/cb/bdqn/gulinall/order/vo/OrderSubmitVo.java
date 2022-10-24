package cb.bdqn.gulinall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {

    // 地址
    private Long addrId;

    // 支付方式
    private Integer payType;

    // 无需提交商品信息再去购物车查询一次

    // 放重令牌
    private String orderToken;

    // 应付价格  这里做验价处理
    private BigDecimal totalPrice;

    // 订单备注
    private String note;

    // 用户相关信息，可以在登录成功后，可以在session中获取出来。
}
