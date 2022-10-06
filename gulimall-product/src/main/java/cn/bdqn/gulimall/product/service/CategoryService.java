package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> withCategoryTree();

    void removeCategoryByIds(List<Long> asList);

    Long[] findAttrGroupLondPath(Long attrGroupId);

    void updateDetil(CategoryEntity category);

    List<CategoryEntity> selectBaseMenus();

    Map<String, List<Catelog2Vo>> searchLevelMenus();
}

