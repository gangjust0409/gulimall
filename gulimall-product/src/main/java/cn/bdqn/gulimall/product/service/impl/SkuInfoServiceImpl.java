package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.product.dao.SkuInfoDao;
import cn.bdqn.gulimall.product.entity.SkuImagesEntity;
import cn.bdqn.gulimall.product.entity.SkuInfoEntity;
import cn.bdqn.gulimall.product.entity.SpuInfoDescEntity;
import cn.bdqn.gulimall.product.service.*;
import cn.bdqn.gulimall.product.vo.SkuItemAttrVo;
import cn.bdqn.gulimall.product.vo.SkuItemVo;
import cn.bdqn.gulimall.product.vo.SpuItemBaseAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    /**
     * key=1&catelogId=225&brandId=15&min=2&max=3
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        // 添加条件
        String key = (String) params.get("key");
        if (! StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (! StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (! StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (! StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (! StringUtils.isEmpty(max)) {
            if (new BigDecimal(max).compareTo(new BigDecimal("0")) == 1) {
                queryWrapper.le("price", max);
            }
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuById(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public SkuItemVo items(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        // 异步编排
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {

            // sku 基本信息
            SkuInfoEntity info = this.getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);

        CompletableFuture<Void> spuItemFuture = infoFuture.thenAcceptAsync(res -> {

            // 获取 spu 的销售属性组合
            List<SkuItemAttrVo> skuItemAttrVos = skuSaleAttrValueService.getSaleAttrValue(res.getSpuId());
            skuItemVo.setAttrs(skuItemAttrVos);
        }, executor);

        CompletableFuture<Void> spuDescFuture = infoFuture.thenAcceptAsync(res -> {

            // 获取 spu 的介绍
            SpuInfoDescEntity descEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuDesc(descEntity);
        }, executor);

        CompletableFuture<Void> spuAttrFuture = infoFuture.thenAcceptAsync(res -> {

            // 获取 spu 的规格参数
            List<SpuItemBaseAttrVo> vos = attrGroupService.getAttrGroupWithBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(vos);
        }, executor);

        // 因为这个不依赖其他的future，所以
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {

            // sku 展示的图片信息  ，pms_sku-images
            List<SkuImagesEntity> images = skuImagesService.getSkuImages(skuId);
            skuItemVo.setImages(images);
        }, executor);

        // 必须全部执行完才返回
        CompletableFuture.allOf(spuItemFuture,spuDescFuture,spuAttrFuture,imagesFuture).get();


        return skuItemVo;
    }

}