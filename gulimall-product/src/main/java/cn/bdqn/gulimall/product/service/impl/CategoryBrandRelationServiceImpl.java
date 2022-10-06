package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.product.dao.BrandDao;
import cn.bdqn.gulimall.product.dao.CategoryDao;
import cn.bdqn.gulimall.product.entity.BrandEntity;
import cn.bdqn.gulimall.product.entity.CategoryEntity;
import cn.bdqn.gulimall.product.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;

import cn.bdqn.gulimall.product.dao.CategoryBrandRelationDao;
import cn.bdqn.gulimall.product.entity.CategoryBrandRelationEntity;
import cn.bdqn.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetil(CategoryBrandRelationEntity categoryBrandRelation) {
        // 获取到id
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        // 根据id查找并保存，当修改品牌属性时，也会修改该关联分类
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void findUpdateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);

        this.update(categoryBrandRelationEntity, new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void findUpdateCategory(Long catId, String name) {
        this.baseMapper.updateDetil(catId, name);
        log.info("修改成功！");
    }

    @Override
    public List<BrandEntity> getBandsByCateId(Long cateId) {
        List<CategoryBrandRelationEntity> catelogId = this.baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", cateId));

        // 根据后面需要根据分类id获取详情信息

        List<BrandEntity> collect = catelogId.stream().map(item -> {
            Long brandId = item.getBrandId();
            BrandEntity byId = brandService.getById(brandId);
            return byId;
        }).collect(Collectors.toList());

        return collect;
    }

}