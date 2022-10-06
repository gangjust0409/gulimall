package cb.bdqn.gulinall.coupon.dao;

import cb.bdqn.gulinall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:15:49
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
