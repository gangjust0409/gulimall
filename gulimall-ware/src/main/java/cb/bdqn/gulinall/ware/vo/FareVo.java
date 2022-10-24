package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    // 地址
    private MemberAddressVo address;

    // 运费
    private BigDecimal fare;

}
