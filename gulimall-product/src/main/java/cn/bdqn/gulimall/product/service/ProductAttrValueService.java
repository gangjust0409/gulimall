package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.product.vo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchAttr(List<ProductAttrValueEntity> baseAttrs);

    List<ProductAttrValueEntity> baseListforspu(Long spuId);

    void updateBatchBySpuId(Long spuId, List<ProductAttrValueEntity> entity);
}

