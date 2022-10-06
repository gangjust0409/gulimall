package cb.bdqn.gulinall.ware.service.impl;

import cb.bdqn.gulinall.ware.entity.PurchaseDetailEntity;
import cb.bdqn.gulinall.ware.service.PurchaseDetailService;
import cb.bdqn.gulinall.ware.service.WareSkuService;
import cb.bdqn.gulinall.ware.vo.DoneVo;
import cb.bdqn.gulinall.ware.vo.MergeVo;
import cb.bdqn.gulinall.ware.vo.PurchaseDoneItemVo;
import cn.bdqn.gulimall.constant.WareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;

import cb.bdqn.gulinall.ware.dao.PurchaseDao;
import cb.bdqn.gulinall.ware.entity.PurchaseEntity;
import cb.bdqn.gulinall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void merge(MergeVo mergeVo) {
        // 分为两种情况，一种是分配员工，和不分配员工，不分配员工重新创建，否则不创建
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            // 改变状态
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }

        //TODD 这里需要判断除了创建和已分配之外是不可以合并的
            List<Long> items = mergeVo.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                // 创建新的需求需要关联一个采购单
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            // 批量修改
            purchaseDetailService.updateBatchById(collect);

        // 每次更新需要更新表里的updatetime字段
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
        log.info("采购单保存成功！");
    }

    @Override
    public void received(Long[] ids) {
        // 先查出采购单必须是创建或者已分配状态下
        List<Long> longList = Arrays.asList(ids);
        List<PurchaseEntity> collect = longList.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(purchaseEntity -> {
            if (purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(purchaseEntity -> {
            // 设置新状态
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            purchaseEntity.setUpdateTime(new Date());
            return purchaseEntity;
        }).collect(Collectors.toList());

        // 改变采购单状态
        this.updateBatchById(collect);

        // 修改订单需求的状态
        collect.forEach(purchaseEntity -> {
            // 根据采购单id查出所有采购需求单的信息并修改状态
            List<PurchaseDetailEntity> purchaseDetailEntitys = purchaseDetailService.listByPurchase(purchaseEntity.getId());
            List<PurchaseDetailEntity> collect1 = purchaseDetailEntitys.stream().map(purchaseDetailEntity -> {
                PurchaseDetailEntity purchaseDetailEntity1 = new PurchaseDetailEntity();
                purchaseDetailEntity1.setId(purchaseDetailEntity.getId());
                purchaseDetailEntity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity1;
            }).collect(Collectors.toList());
            // 批量修改
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(DoneVo doneVo) {
        Long voId = doneVo.getId();

        Boolean flag = true;
        // 2 改变采购单项的状态
        List<PurchaseDoneItemVo> items = doneVo.getItems();
        // 存储要修改数据
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseDoneItemVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            // 采购单有异常情况下
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERRO.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(item.getStatus());
                // 3 保存到库存   不只更新状态
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
                flag = true;
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        // 1 改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(voId);

        purchaseEntity.setStatus(flag? WareConstant.PurchaseStatusEnum.FINISHED.getCode() : WareConstant.PurchaseStatusEnum.HASERRO.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}