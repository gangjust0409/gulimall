package cn.bdqn.gulimall.product.vo;

import lombok.Data;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/24
 */
@Data
public class AttrGoupVO extends AttrVo {

    private String attrGroupName;

    private String catelogName;

    private Long[] catelogPath;

}
