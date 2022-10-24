package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

@Data
public class SkuWareResultVo {

    private Long skuId;

    private Integer num;

    private Boolean locked; //是否锁库存成功

}
