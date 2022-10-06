package cb.bdqn.gulinall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:15:48
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

