package cb.bdqn.gulinall.ware.controller;

import cb.bdqn.gulinall.ware.entity.WareSkuEntity;
import cb.bdqn.gulinall.ware.service.WareSkuService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * 商品库存
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
@RestController
@RequestMapping("/ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    // 远程传给sku
    @PostMapping("/hasstock")
    public R wareSku(@RequestBody List<Long> skuIds) {
        List<SkuStockVo> list = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().setData(list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
