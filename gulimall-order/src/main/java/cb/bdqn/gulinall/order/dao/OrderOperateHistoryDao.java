package cb.bdqn.gulinall.order.dao;

import cb.bdqn.gulinall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:15
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
