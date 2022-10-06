package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.AttrEntity;
import cn.bdqn.gulimall.product.vo.AttrGoupVO;
import cn.bdqn.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveGroup(AttrVo attr);

    PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId, String attrType);

    AttrGoupVO getAttrInfoVo(Long attrId);

    void updateAttr(AttrGoupVO attr);

    PageUtils queryNoAttrRelation(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的属性中，查询出可以检索的属性
     * @param ids
     * @return
     */
    List<Long> selectSearchAttrId(Collection<Long> ids);
}

