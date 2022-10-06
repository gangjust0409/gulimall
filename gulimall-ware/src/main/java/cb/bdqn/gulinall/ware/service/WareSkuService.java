package cb.bdqn.gulinall.ware.service;

import cn.bdqn.gulimall.vo.SkuStockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询出每个sku的库存总数
     * @param skuIds
     * @return
     */
    List<SkuStockVo> getSkuHasStock(List<Long> skuIds);
}

