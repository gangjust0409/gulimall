package cb.bdqn.gulinall.coupon.service.impl;

import cb.bdqn.gulinall.coupon.dao.SeckillSessionDao;
import cb.bdqn.gulinall.coupon.entity.SeckillSessionEntity;
import cb.bdqn.gulinall.coupon.entity.SeckillSkuRelationEntity;
import cb.bdqn.gulinall.coupon.service.SeckillSessionService;
import cb.bdqn.gulinall.coupon.service.SeckillSkuRelationService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {


    DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLetes3DaySession() {

        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));

        // 需要在查询每个活动下关联的商品信息
        if (list != null && list.size() > 0) {
            List<SeckillSessionEntity> collect = list.stream().map(session -> {
                List<SeckillSkuRelationEntity> relationSkus = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
                session.setRelationSkus(relationSkus);
                return session;
            }).collect(Collectors.toList());
            return list;
        }
        return null;
    }

    // 当前时间
    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime dateTime = LocalDateTime.of(now, min);

        return dateTime.format(pattern);
    }

    // 2 天后的时间
    private String endTime() {
        LocalDate date = LocalDate.now().plusDays(2);

        LocalTime min = LocalTime.MAX;
        LocalDateTime dateTime = LocalDateTime.of(date, min);

        return dateTime.format(pattern);
    }
}