package cb.bdqn.gulinall.coupon.service.impl;

import cb.bdqn.gulinall.coupon.dao.SeckillSkuRelationDao;
import cb.bdqn.gulinall.coupon.entity.SeckillSkuRelationEntity;
import cb.bdqn.gulinall.coupon.service.SeckillSkuRelationService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        // 需要根据promotionSessionId查询
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();
        String promotionSessionId = (String) params.get("promotionSessionId");
        // 如果有条件
        if (!StringUtils.isEmpty(promotionSessionId)) {
            queryWrapper.eq("promotion_session_id",promotionSessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}