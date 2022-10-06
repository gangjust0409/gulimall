package cn.bdqn.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.bdqn.gulimall.product.entity.AttrEntity;
import cn.bdqn.gulimall.product.service.AttrAttrgroupRelationService;
import cn.bdqn.gulimall.product.service.AttrService;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.product.vo.AttrGroupWithAttrsVo;
import cn.bdqn.gulimall.product.vo.AttrRelationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.bdqn.gulimall.product.entity.AttrGroupEntity;
import cn.bdqn.gulimall.product.service.AttrGroupService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;



/**
 * 属性分组
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:22
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable(value = "catId") Long catId){
        //PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, catId);

        return R.ok().put("page", page);
    }

    /**
     * product/attrgroup/4/attr/relation
     * 查询出关联的分组信息
     * @param attrgroupId
     * @return
     */
    @GetMapping("{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {

        List<AttrEntity> data = attrGroupService.attrRelation(attrgroupId);

        return R.ok().put("data", data);
    }

    // product/attrgroup/1/noattr/relation
    @GetMapping("{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryNoAttrRelation(params, attrgroupId);


        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] path = categoryService.findAttrGroupLondPath(attrGroup.getCatelogId());
        attrGroup.setAttrGroupIdPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    // 发布商品，规格参数/product/attrgroup/225/withattr
    @GetMapping("/{catelogId}/withattr")
    public R attrGroupCatelogIdWithattr(@PathVariable Long catelogId) {
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrByCatelogId(catelogId);

        return R.ok().put("data", vos);
    }


    /**
     * product/attrgroup/attr/relation
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody AttrRelationVo[] attrRelationVo) {
        attrAttrgroupRelationService.saveRelation(attrRelationVo);
        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * product/attrgroup/attr/relation/delete
     * 批量删除和删除关联信息
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrRelationVo[] vos) {
        attrGroupService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
