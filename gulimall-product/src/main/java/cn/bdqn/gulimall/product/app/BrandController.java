package cn.bdqn.gulimall.product.app;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.common.validator.group.AddGroup;
import cn.bdqn.gulimall.common.validator.group.UpdateGroup;
import cn.bdqn.gulimall.common.validator.group.UpdateStatusGroup;
import cn.bdqn.gulimall.product.entity.BrandEntity;
import cn.bdqn.gulimall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 品牌
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;



    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/infos")
    public R brandIds(@RequestParam("brandId")List<Long> brandId){
        List<BrandEntity> brands = brandService.getBrandsByIds(brandId);

        return R.ok().put("brand", brands);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * @Valid 开启后台服务校验
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:brand:save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand/*, BindingResult result*/){
        // 如果校验不成功
      /*  if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            result.getFieldErrors().forEach(item -> {
                // 属性名
                String fileId = item.getField();
                String message = item.getDefaultMessage();
                errorMap.put(fileId, message);
            });
            return R.error(400, "存在错误").put("data", errorMap);
        } else {

        }*/
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
		brandService.updateDetil(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
