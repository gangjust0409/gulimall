package cb.bdqn.gulinall.order.dao;

import cb.bdqn.gulinall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:17
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
