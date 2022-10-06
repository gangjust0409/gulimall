package cn.bdqn.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuItemBaseAttrVo {
    private String groupName;
    private List<SpuGroupAttrVo> groupValues;
}
