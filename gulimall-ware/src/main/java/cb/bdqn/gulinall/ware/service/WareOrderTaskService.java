package cb.bdqn.gulinall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cb.bdqn.gulinall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

