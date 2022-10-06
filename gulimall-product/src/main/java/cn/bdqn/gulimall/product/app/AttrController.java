package cn.bdqn.gulimall.product.app;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.product.entity.ProductAttrValueEntity;
import cn.bdqn.gulimall.product.service.AttrService;
import cn.bdqn.gulimall.product.service.ProductAttrValueService;
import cn.bdqn.gulimall.product.vo.AttrGoupVO;
import cn.bdqn.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * 商品属性
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    // /attr/base/listforspu/22
    @GetMapping("/base/listforspu/{spuId}")
    public R baseListforspu(@PathVariable Long spuId) {
        List<ProductAttrValueEntity> entityList = attrValueService.baseListforspu(spuId);
        return R.ok().put("data", entityList);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable("attrType") String attrType,
                      @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.queryPageByCatelogId(params, catelogId, attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrGoupVO attr = attrService.getAttrInfoVo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveGroup(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrGoupVO attr){
		//attrService.updateById(attr);
        attrService.updateAttr(attr);

        return R.ok();
    }

    //update/22
    @RequestMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R update(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> entity){
        attrValueService.updateBatchBySpuId(spuId, entity);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
