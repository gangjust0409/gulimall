package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateDetil(@Param("catId") Long catId, @Param("name") String name);
}
