package cb.bdqn.gulinall.coupon.service;

import cn.bdqn.gulimall.to.SkuReductioinTo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:15:50
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductioinTo skuReductioinTo);
}

