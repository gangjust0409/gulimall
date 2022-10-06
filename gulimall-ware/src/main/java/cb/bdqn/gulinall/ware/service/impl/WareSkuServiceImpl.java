package cb.bdqn.gulinall.ware.service.impl;

import cb.bdqn.gulinall.ware.dao.WareSkuDao;
import cb.bdqn.gulinall.ware.entity.WareSkuEntity;
import cb.bdqn.gulinall.ware.feign.ProductFeignService;
import cb.bdqn.gulinall.ware.service.WareSkuService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.vo.SkuStockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            skuStockVo.setHasStock(count==null?true:count > 0);
            return skuStockVo;
        }).collect(Collectors.toList());
        return collect;
    }

}