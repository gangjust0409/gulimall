package cn.bdqn.gulimall.product.dao;

import cn.bdqn.gulimall.constant.AttrConstant;
import cn.bdqn.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:20
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
    
}
