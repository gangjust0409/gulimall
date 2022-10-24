package cb.bdqn.gulinall.ware.service.impl;

import cb.bdqn.gulinall.ware.dao.WareSkuDao;
import cb.bdqn.gulinall.ware.entity.WareOrderTaskDetailEntity;
import cb.bdqn.gulinall.ware.entity.WareOrderTaskEntity;
import cb.bdqn.gulinall.ware.entity.WareSkuEntity;
import cb.bdqn.gulinall.ware.exception.NoHasStockException;
import cb.bdqn.gulinall.ware.feign.OrderFeignService;
import cb.bdqn.gulinall.ware.feign.ProductFeignService;
import cb.bdqn.gulinall.ware.service.WareOrderTaskDetailService;
import cb.bdqn.gulinall.ware.service.WareOrderTaskService;
import cb.bdqn.gulinall.ware.service.WareSkuService;
import cb.bdqn.gulinall.ware.vo.OrderItem;
import cb.bdqn.gulinall.ware.vo.OrderVo;
import cb.bdqn.gulinall.ware.vo.OrderWareVo;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.to.mq.OrderTo;
import cn.bdqn.gulimall.to.mq.WareLockDetail;
import cn.bdqn.gulimall.to.mq.WareLockSkuTo;
import cn.bdqn.gulimall.vo.SkuStockVo;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    /**
     * 注入feign
     */
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 如果库存表没有数据，则要添加，否则修改
        List<WareSkuEntity> entityList = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entityList == null || entityList.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            try {
                // 设置sku名字，需要远程查出  可能是出现异常，如何事务全部回滚，
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    String skuName = (String) skuInfo.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {

            }
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            this.save(wareSkuEntity);
        } else {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuStockVo> getSkuHasStock(List<Long> skuIds) {
        // select sum(stock - stock_locked) from wms_ware_sku where sku_id = 1

        List<SkuStockVo> collect = skuIds.stream().map(skuId -> {
            SkuStockVo skuStockVo = new SkuStockVo();
            // 查询当前 sku 的总库存数
            Long count = baseMapper.getSkuHasStock(skuId);
            skuStockVo.setSkuId(skuId);
            skuStockVo.setHasStock(count==null?false:count > 0);
            return skuStockVo;
        }).collect(Collectors.toList());
        return collect;
    }

    // 解锁库存的方法
    private void unlock(Long skuId, Long wareId, Integer num, Long deailId){
        baseMapper.unlock(skuId,wareId,num);
        // 修改订单工作详情表的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(deailId);
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);
        System.out.println("库存修改状态成功！"+deailId);
    }
    private void unlock(Long skuId, Long wareId, Integer num, Long deailId, Long taskId){
        // 查询订单工作单
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(taskId);
        // 根据订单号查询是否付款成功
        R r = orderFeignService.orderByOrderSn(orderTaskEntity.getOrderSn());
        OrderVo data = r.getData(new TypeReference<OrderVo>() {
        });

        if (data != null && data.getStatus() == 0) {
            baseMapper.unlock(skuId,wareId,num);
        }
        // 修改订单工作详情表的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(deailId);
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);
        System.out.println("库存修改状态成功！"+deailId);
    }

    /**
     * 1 下订单成功，订单过期没有支付被系统自动取消、被用户手动取消。都要解锁库存
     * 2 下订单成功。库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存自动解锁
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLock(OrderWareVo vo) {
        /**
         * 保存库存工作单的的详情
         * 追溯
         */
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity();
        orderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(orderTaskEntity);

        // 锁库存
        List<OrderItem> locks = vo.getLocks();
        // 查询有货的仓库id
        List<WareHasStockLock> collect = locks.stream().map(item -> {
            Long skuId = item.getSkuId();
            WareHasStockLock stockLock = new WareHasStockLock();
            stockLock.setSkuId(skuId);
            stockLock.setNum(item.getCount());
            // 查询有库存的仓库id
            List<Long> wareIds = baseMapper.listWareIds(skuId);
            stockLock.setWareId(wareIds);
            return stockLock;
        }).collect(Collectors.toList());
        // 锁定库存
        for (WareHasStockLock hasStockLock : collect) {
            Long skuId = hasStockLock.getSkuId();
            List<Long> wareIds = hasStockLock.getWareId();
            Boolean hasStock = false; // 如果锁住，那么赋值为true
            if (wareIds == null || wareIds.size() == 0) {
                // 没有库存了
                throw new NoHasStockException(skuId);
            }
            // 遍历可以用的仓库
            for (Long wareId : wareIds) {
                // 锁定库存的方法
                Long res = baseMapper.lockWare(skuId, wareId, hasStockLock.getNum());
                if (res == 1) {
                    // 有库存
                    hasStock = true;
                    // 告诉mq锁定成功，发送消息
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                    detailEntity.setSkuId(skuId);
                    detailEntity.setSkuNum(hasStockLock.getNum());
                    detailEntity.setTaskId(orderTaskEntity.getId());
                    detailEntity.setWareId(wareId);
                    detailEntity.setLockStatus(1);
                    wareOrderTaskDetailService.save(detailEntity);
                    WareLockSkuTo lockSkuTo = new WareLockSkuTo();
                    lockSkuTo.setTaskId(orderTaskEntity.getId());
                    WareLockDetail detail = new WareLockDetail();
                    BeanUtils.copyProperties(detailEntity, detail);
                    lockSkuTo.setDetail(detail);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockSkuTo);

                    break;
                } else {
                    // 没有库存了
                    throw new NoHasStockException(skuId);
                }
            }
        }
        // 能到这就锁库成功
        return true;
    }

    @Override
    public void unLockStock(WareLockSkuTo to) {
        // 查询数据库工作的详情，有的话就解锁，没有就不用解锁。
        WareLockDetail detail = to.getDetail();
        Long detailId = detail.getId();
        WareOrderTaskDetailEntity taskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailEntity != null) {
            // 需解锁
            // 订单不存在也需要解锁（可能是订单服务其他报错，引起的错误，也有回滚）
            // 订单的状态
            // 已取消  需要解锁   其他状态：不用解锁
            Long taskId = to.getTaskId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(taskId);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.orderByOrderSn(orderSn);
            if (r.getCode() == 0) {
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                System.out.println(data+"[]");
                if (data == null || data.getStatus() == 4) {
                    if (detail.getLockStatus() == 1) {
                        // 解锁方法
                        unlock(detail.getSkuId(),detail.getWareId(), detail.getSkuNum(), detail.getId());
                    }
                }
            } else {
                // 出现异常，也把消息保存在队列中
                throw new RuntimeException();
            }
        } else {
            // 无需解锁
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查订单状态新建状态，什么都不做就走了
     * 导致卡顿的订单，永远不能解锁库存
     * @param order
     */
    @Override
    public void unLockStock(OrderTo order) {
        // 查询库存的最新状态
        String orderSn = order.getOrderSn();
        // 查询订单工作单id
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", orderTaskEntity.getId()).eq("lock_status", 1));
        // 调用解库存的方法
        for (WareOrderTaskDetailEntity entity : list) {
            unlock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId(),entity.getTaskId());
        }
        System.out.println("在关单之前解除库存成功！");
    }

    @Data
    class WareHasStockLock{
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }

}