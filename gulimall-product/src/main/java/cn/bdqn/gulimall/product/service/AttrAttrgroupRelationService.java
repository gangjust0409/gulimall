package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.product.vo.AttrRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveRelation(AttrRelationVo[] attrRelationVo);
}

