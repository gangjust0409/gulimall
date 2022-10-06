package cb.bdqn.gulinall.ware.service;

import cb.bdqn.gulinall.ware.vo.DoneVo;
import cb.bdqn.gulinall.ware.vo.MergeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void merge(MergeVo mergeVo);

    void received(Long[] ids);

    void done(DoneVo doneVo);
}

