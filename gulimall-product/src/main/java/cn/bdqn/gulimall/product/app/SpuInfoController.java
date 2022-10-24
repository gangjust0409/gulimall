package cn.bdqn.gulimall.product.app;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.product.entity.SpuInfoEntity;
import cn.bdqn.gulimall.product.service.SpuInfoService;
import cn.bdqn.gulimall.product.vo.SpuSavevVo;
import cn.bdqn.gulimall.vo.OrderSpuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * spu信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:20
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    // skuid获取spu信息
    @GetMapping("/spuInfo/{id}")
    public R spuInfoBySkuId(@PathVariable("id") Long skuId){
        OrderSpuInfoVo spuInfo = spuInfoService.getSpuInfoBySkuId(skuId);

        return R.ok().setData(spuInfo);
    }

    // 16/up 上架商品
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.querySpuInfoByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSavevVo vo){
		//spuInfoService.save(spuInfo);

        spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
