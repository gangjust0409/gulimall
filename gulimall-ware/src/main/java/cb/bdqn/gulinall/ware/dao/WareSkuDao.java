package cb.bdqn.gulinall.ware.dao;

import cb.bdqn.gulinall.ware.entity.WareSkuEntity;
import cn.bdqn.gulimall.vo.SkuStockVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuHasStock(Long skuId);
}
