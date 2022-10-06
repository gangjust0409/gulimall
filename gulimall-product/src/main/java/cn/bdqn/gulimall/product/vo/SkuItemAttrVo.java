package cn.bdqn.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemAttrVo {

    private Long attrId;
    private String attrName;
    private List<AttrSkuWithIdVo> attrValue;

}
