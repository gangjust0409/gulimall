package cn.bdqn.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.bdqn.gulimall.product.entity.CategoryEntity;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.common.utils.R;



/**
 * 商品三级分类
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity> categoryEntities = categoryService.withCategoryTree();
        return R.ok().put("data", categoryEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
   // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetil(category);

        return R.ok();
    }

    @RequestMapping(value = "/update/sort", method = RequestMethod.POST)
    //@RequiresPermissions("product:category:update")
    public R batchUpdate(@RequestBody CategoryEntity[] categorys){
        categoryService.updateBatchById(Arrays.asList(categorys));

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		// categoryService.removeByIds(Arrays.asList(catIds));

        categoryService.removeCategoryByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
