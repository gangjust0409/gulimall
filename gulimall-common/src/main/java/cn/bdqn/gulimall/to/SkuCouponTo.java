package cn.bdqn.gulimall.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/3
 */
@Data
public class SkuCouponTo {

    private Long skuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
