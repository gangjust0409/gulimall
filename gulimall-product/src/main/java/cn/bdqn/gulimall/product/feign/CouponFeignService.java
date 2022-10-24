package cn.bdqn.gulimall.product.feign;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.product.feign.fallback.CouponFeignFallback;
import cn.bdqn.gulimall.to.SkuCouponTo;
import cn.bdqn.gulimall.to.SkuReductioinTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/3
 */
@Component
@FeignClient(value = "gulimall-coupon",fallback = CouponFeignFallback.class)
public interface CouponFeignService {

    /**
     * 1 @RequestBody 将这个对象转为json
     * 2 找到 guilimall-coupon服务，给coupon/spubounds/save发送请求
     *      将上一步转的json放在请求体位置，发送请求
     *  3 对方服务收到请求。请求体有json数据
     *     @ResquestBody SpuBoundsEntity spuBounds 将请求体的json转为 SpuBoundsEntity
     *  只要json数据模型是兼容的，双方五福无需使用用一个to
     * @param skuCouponTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveBounds(@RequestBody SkuCouponTo skuCouponTo) ;


    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuRecution(SkuReductioinTo skuReductioinTo);
}
