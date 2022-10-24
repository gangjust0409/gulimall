package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.product.entity.SkuSaleAttrValueEntity;
import cn.bdqn.gulimall.product.vo.SkuItemAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:20
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemAttrVo> getSaleAttrValue(@Param("spuId") Long spuId);

    List<String> getSkuAttr(@Param("skuId") Long skuId);
}
