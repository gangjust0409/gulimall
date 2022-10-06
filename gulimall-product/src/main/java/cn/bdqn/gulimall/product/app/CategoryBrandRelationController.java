package cn.bdqn.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.bdqn.gulimall.product.entity.BrandEntity;
import cn.bdqn.gulimall.product.vo.BrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.bdqn.gulimall.product.entity.CategoryBrandRelationEntity;
import cn.bdqn.gulimall.product.service.CategoryBrandRelationService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/catelog/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        System.out.println("brandId " + brandId);
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId)
        );
        System.out.println("data " + data);
        return R.ok().put("data", data);
    }

    /**
     * /product/categorybrandrelation/brands/list
     * 点击发布商品时选择分类查出品牌信息
     * controller 处理请求，接受和校验数据
     *
     * service 接受controler传来的数据，处理业务逻辑
     * controller 接收service处理好的数据，并封装返回vo
     *     product/categorybrandrelation
     * api/product/categorybrandrelation/brands/list
     * @return
     */
    @GetMapping("/brands/list")
    public R brandsList(@RequestParam("catId") Long cateId) {
       List<BrandEntity> data =  categoryBrandRelationService.getBandsByCateId(cateId);

       // 只需要传入前端需要显示的字段
        List<BrandVo> collect = data.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            System.err.println(item+"debug");
            if (item != null){
                brandVo.setBrandId(item.getBrandId());
                brandVo.setBrandName(item.getName());
            }
            return brandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data", collect);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }




    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		//categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveDetil(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
