package cn.bdqn.gulimall.product.vo;

import cn.bdqn.gulimall.product.entity.SkuImagesEntity;
import cn.bdqn.gulimall.product.entity.SkuInfoEntity;
import cn.bdqn.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    //sku 基本信息
    private SkuInfoEntity info;

    private boolean hasStock = true;

    // sku 展示的图片信息  ，pms_sku-images
    private List<SkuImagesEntity> images;

    // 获取 spu 的销售属性组合
    private List<SkuItemAttrVo> attrs;

    // 获取 spu 的介绍
    private SpuInfoDescEntity spuDesc;

    // 获取 spu 的规格参数
    private List<SpuItemBaseAttrVo> groupAttrs;

    // 商品秒杀
    private SeckillInfoVo seckillInfo;

}
