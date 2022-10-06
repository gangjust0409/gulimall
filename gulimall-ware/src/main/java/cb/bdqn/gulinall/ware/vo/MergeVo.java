package cb.bdqn.gulinall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * items: [1, 2]
 * purchaseId: 1
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/5
 */
@Data
public class MergeVo {

    private Long purchaseId;
    private List<Long> items;

}
