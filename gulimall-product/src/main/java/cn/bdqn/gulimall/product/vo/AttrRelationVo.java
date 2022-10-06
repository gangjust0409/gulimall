package cn.bdqn.gulimall.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于存储从页面获取的id
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/25
 */
@Data
public class AttrRelationVo implements Serializable {

    private static final long serialVersionUID = -1242493306307174690L;

    private Long attrId;
    private Long attrGroupId;

}
