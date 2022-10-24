package cb.bdqn.gulinall.order.controller;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.service.OrderService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * 订单
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:17
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 支付请求
    @GetMapping("/order/state/{orderSn}")
    public R orderByOrderSn(@PathVariable String orderSn){
        OrderEntity order = orderService.getOrderState(orderSn);
        return R.ok().setData(order);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }

    // 查询订单信息
    @PostMapping("/orderWithMember")
    public R queryOrders(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryOrders(params);
        return R.ok().setData(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
