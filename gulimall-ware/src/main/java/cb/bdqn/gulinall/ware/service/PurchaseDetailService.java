package cb.bdqn.gulinall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listByPurchase(Long id);

    PurchaseDetailEntity getPurchaseDetailByPurchaseId(Long purchaseId);
}

