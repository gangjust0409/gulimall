package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 商品属性
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrId(@Param("ids") Collection<Long> ids);
}
