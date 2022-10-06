package cb.bdqn.gulinall.coupon.dao;

import cb.bdqn.gulinall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:15:50
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
