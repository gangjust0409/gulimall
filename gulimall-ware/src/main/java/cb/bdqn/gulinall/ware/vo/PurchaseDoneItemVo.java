package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/5
 */
@Data
public class PurchaseDoneItemVo {

    private Long itemId;
    private int status;
    private String reason;

}
