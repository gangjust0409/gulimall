package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.AttrEntity;
import cn.bdqn.gulimall.product.entity.AttrGroupEntity;
import cn.bdqn.gulimall.product.vo.AttrGroupWithAttrsVo;
import cn.bdqn.gulimall.product.vo.AttrRelationVo;
import cn.bdqn.gulimall.product.vo.SpuItemBaseAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    List<AttrEntity> attrRelation(Long catelogId);

    void deleteRelation(AttrRelationVo[] vos);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCatelogId(Long catelogId);

    List<SpuItemBaseAttrVo> getAttrGroupWithBySpuId(Long spuId, Long catalogId);
}

