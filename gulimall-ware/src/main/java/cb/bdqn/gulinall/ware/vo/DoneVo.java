package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *  {
 *     "id": 3,
 *     "items": [
 *         { "itemId":4, "status": 3,"reason":"" },
 *         { "itemId":5, "status": 3,"reason":"" },
 *         { "itemId":6, "status": 3,"reason":"" }
 *     ]
 * }
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/5
 */
@Data
public class DoneVo {

    @NotNull
    private Long id;

    private List<PurchaseDoneItemVo> items;

}
