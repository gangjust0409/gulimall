package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.AttrGroupEntity;
import cn.bdqn.gulimall.product.vo.SpuItemBaseAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemBaseAttrVo> getAttrGroupWithBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
