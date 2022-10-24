package cb.bdqn.gulinall.order.service.impl;

import cb.bdqn.gulinall.order.dao.OrderItemDao;
import cb.bdqn.gulinall.order.entity.OrderItemEntity;
import cb.bdqn.gulinall.order.service.OrderItemService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * seata 只能单条插入，批量插入会报错 NotSupportYetException
     * @param orderItems
     */
    @Override
    public void insertBatch(List<OrderItemEntity> orderItems) {
        if (orderItems != null && orderItems.size() > 0) {
            for (OrderItemEntity orderItem : orderItems) {
                // 单个插入
                this.save(orderItem);
            }
        }
    }

}