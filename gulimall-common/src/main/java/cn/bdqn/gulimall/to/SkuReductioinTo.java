package cn.bdqn.gulimall.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/3
 */
@Data
public class SkuReductioinTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;

    List<MemberPrice> memberPrices;

}
