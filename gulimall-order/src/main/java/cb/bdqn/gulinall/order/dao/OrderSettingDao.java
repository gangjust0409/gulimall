package cb.bdqn.gulinall.order.dao;

import cb.bdqn.gulinall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:16
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
