package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.SkuSaleAttrValueEntity;
import cn.bdqn.gulimall.product.vo.SkuItemAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:20
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchValueAttrs(List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList);

    List<SkuItemAttrVo> getSaleAttrValue(Long spuId);

}

