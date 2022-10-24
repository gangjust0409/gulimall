package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.constant.AttrConstant;
import cn.bdqn.gulimall.product.dao.SpuInfoDao;
import cn.bdqn.gulimall.product.entity.*;
import cn.bdqn.gulimall.product.feign.CouponFeignService;
import cn.bdqn.gulimall.product.feign.SearchFeignService;
import cn.bdqn.gulimall.product.feign.WareSkuFeignService;
import cn.bdqn.gulimall.product.service.*;
import cn.bdqn.gulimall.product.vo.*;
import cn.bdqn.gulimall.to.SkuCouponTo;
import cn.bdqn.gulimall.to.SkuReductioinTo;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import cn.bdqn.gulimall.vo.OrderSpuInfoVo;
import cn.bdqn.gulimall.vo.SkuStockVo;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    AttrService attrService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService attrValueService;

    // 远程访问
    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareSkuFeignService wareSkuFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @GlobalTransactional // 后台管理系统使用与at模式
    @Transactional(rollbackFor = SQLException.class)
    @Override
    public void saveSpuInfo(SpuSavevVo vo) {
        // 1 保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        // 复制
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBatchSpuInfo(spuInfoEntity);

        //2 保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuDescInfo(spuInfoDescEntity);

        //3保存spu的图片机 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());

        // 保存
        productAttrValueService.saveBatchAttr(collect);

        // 5 保存当前spu对应的所有sku信息

        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                String defaultSkuImgUrl = "";
                // 获取sku的默认图片
                for (Images img: sku.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultSkuImgUrl = img.getImgUrl();
                    }
                }

                //5.1 sku的基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultSkuImgUrl);

                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                //5.2sku的图片信息 pms_sku_iamges
                List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                    // 返回true就需要，false就过滤掉
                }).filter(img -> ! StringUtils.isEmpty(img.getImgUrl())).collect(Collectors.toList());

                // 保存
                skuImagesService.saveBatchImages(skuImagesEntities);


                // 5 保存积分信息  微服务之间需要封装一个领域对象 to
                Bounds bounds = vo.getBounds();
                SkuCouponTo skuCouponTo = new SkuCouponTo();
                BeanUtils.copyProperties(bounds, skuCouponTo);
                skuCouponTo.setSkuId(skuId);
                R r1 = couponFeignService.saveBounds(skuCouponTo);
                if (r1.getCode() != 0) {
                    log.error("远程保存spu积分失败！");
                }


                //5.3 sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                // 保存
                attrValueService.saveBatchValueAttrs(skuSaleAttrValueEntityList);


                // 以下跨库，sms

                //5.4 sku的优惠、满减等信息
                SkuReductioinTo skuReductioinTo = new SkuReductioinTo();
                BeanUtils.copyProperties(sku, skuReductioinTo);
                skuReductioinTo.setSkuId(skuId);
                if (skuReductioinTo.getFullCount() > 0 || skuReductioinTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuRecution(skuReductioinTo);
                    if (r.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败！");
                    }
                }


            });
        }




    }

    @Override
    public void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);
    }

    /**
     * status=0&
     * key=10&
     * brandId=15&
     * catelogId=225
     * @param params
     * @return
     */
    @Override
    public PageUtils querySpuInfoByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        // 拼接条件
        // id或者sku名称
        String key = (String) params.get("key");
        if (! StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if (! StringUtils.isEmpty(status) && !"0".equalsIgnoreCase(status)) {
            queryWrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (! StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (! StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 查询所有的 spuId 的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkuById(spuId);
        // 查询所有的skuId
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 查询 sku 的所有被用来检索的规格属性
        // 获取 spuid 下的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseListforspu(spuId);
        System.out.println("规格参数" + productAttrValueEntities);
        // 查询出所有的规格属性id
        List<Long> ids = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

        // 过滤掉用来检索的 attrId
        List<Long> attrIds = attrService.selectSearchAttrId(ids);
        Set<Long> idSet = new HashSet<>(attrIds);

        // 进行过滤 返回每个spu 的规格属性
        List<SkuEsModule.Attrs> skuAttrs = productAttrValueEntities.stream()
                .filter(attr -> idSet.contains(attr.getAttrId())).map(attr -> {
            SkuEsModule.Attrs attrs = new SkuEsModule.Attrs();
            BeanUtils.copyProperties(attr, attrs);
            return attrs;
        }).collect(Collectors.toList());
        log.info("skuattr -> " + skuAttrs);

        Map<Long, Boolean> hasStockMap = null;
        //TODO hasStock hotScore  发送远程调用，库存系统是否有库存
        try{  // 使用 try 原因，可能对方服务未开启，会报错，未开启时，设置为有库存
            R r = wareSkuFeignService.wareSku(skuIds);
            // TypeReference 是一个内部类类型
            hasStockMap = r.getData(new TypeReference<List<SkuStockVo>>(){}).stream().collect(Collectors.toMap(SkuStockVo::getSkuId, item -> item.getHasStock()));

        } catch (Exception e){
            log.error("ware sku 报错了 {}",e);
        }


        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModule> upProducts = skus.stream().map(sku -> {
            // 组装数据
            SkuEsModule skuEsModule = new SkuEsModule();
            BeanUtils.copyProperties(sku, skuEsModule);
            // skuPrice  skuImg    brandName  brandImg catelogName
            skuEsModule.setSkuPrice(sku.getPrice());
            skuEsModule.setSkuImg(sku.getSkuDefaultImg());
            // 查询品牌名和分类名
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            skuEsModule.setBrandName(brandEntity.getName());
            skuEsModule.setBrandImg(brandEntity.getLogo());
            skuEsModule.setCatalogName(categoryEntity.getName());
            // 设置检索属性
            skuEsModule.setAttrs(skuAttrs);
            // 设置是否有库存
            if (finalHasStockMap == null) {
                skuEsModule.setHasStock(true);
            } else {
                skuEsModule.setHasStock(finalHasStockMap.get(sku.getSkuId()));
            }
            // 热度评分。
            skuEsModule.setHotScore(0L);
            return skuEsModule;
        }).collect(Collectors.toList());

        // 将数据保存到 es 中
        R r = searchFeignService.productUp(upProducts);
        if (r.getCode() == 0) {
            // 远程调用成功  状态也要改变
            baseMapper.updateSpuStatus(spuId, AttrConstant.Status.SPU_UP.getCode());
            log.info("SpuInfoServiceImpl---上架成功！");
        } else {
            // TODD 调用失败  重复调用  接口幂等性
        }

    }

    @Override
    public OrderSpuInfoVo getSpuInfoBySkuId(Long skuId) {
        OrderSpuInfoVo spuInfoVo = new OrderSpuInfoVo();
        // 根据skuid获取spuid
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfoEntity = getById(skuInfoEntity.getSpuId());
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
        BeanUtils.copyProperties(spuInfoEntity, spuInfoVo);
        spuInfoVo.setBrandName(brandEntity.getName());
        return spuInfoVo;
    }


}