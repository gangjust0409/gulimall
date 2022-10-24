package cb.bdqn.gulinall.order.dao;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:17
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("out_trade_no") String out_trade_no, @Param("code") Integer code);
}
