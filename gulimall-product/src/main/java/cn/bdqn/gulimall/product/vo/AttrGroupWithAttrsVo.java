package cn.bdqn.gulimall.product.vo;

import cn.bdqn.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/3
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
