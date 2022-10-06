package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetil(CategoryBrandRelationEntity categoryBrandRelation);

    void findUpdateBrand(Long brandId, String name);

    void findUpdateCategory(Long catId, String name);

    List<BrandEntity> getBandsByCateId(Long cateId);
}

