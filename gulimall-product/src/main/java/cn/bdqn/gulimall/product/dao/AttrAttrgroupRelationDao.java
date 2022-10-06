package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchAttrRelation(@Param("relationEntities") List<AttrAttrgroupRelationEntity> relationEntities) ;
}
