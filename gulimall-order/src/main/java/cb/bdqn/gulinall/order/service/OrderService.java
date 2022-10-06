package cb.bdqn.gulinall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:17
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

