package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.product.dao.SkuImagesDao;
import cn.bdqn.gulimall.product.entity.SkuImagesEntity;
import cn.bdqn.gulimall.product.service.SkuImagesService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatchImages(List<SkuImagesEntity> skuImagesEntities) {
        this.saveBatch(skuImagesEntities);
    }

    @Override
    public List<SkuImagesEntity> getSkuImages(Long skuId) {

        return baseMapper.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));
    }

}