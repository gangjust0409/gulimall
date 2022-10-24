package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.SpuInfoEntity;
import cn.bdqn.gulimall.product.vo.SpuSavevVo;
import cn.bdqn.gulimall.vo.OrderSpuInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:20
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSavevVo vo);

    void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils querySpuInfoByCondition(Map<String, Object> params);

    /**
     * 商品上架
     */
    void up(Long spuId);

    OrderSpuInfoVo getSpuInfoBySkuId(Long skuId);
}

