package cb.bdqn.gulinall.order.vo;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 返回到支付页的结果
 */
@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    // 0 成功 其他错误
    private Integer code;

}
