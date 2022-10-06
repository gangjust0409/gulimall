package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
