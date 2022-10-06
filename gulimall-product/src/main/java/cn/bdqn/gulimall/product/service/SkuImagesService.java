package cn.bdqn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchImages(List<SkuImagesEntity> skuImagesEntities);

    List<SkuImagesEntity> getSkuImages(Long skuId);
}

