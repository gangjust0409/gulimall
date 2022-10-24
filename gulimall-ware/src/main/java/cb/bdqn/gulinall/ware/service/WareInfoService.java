package cb.bdqn.gulinall.ware.service;

import cb.bdqn.gulinall.ware.entity.WareInfoEntity;
import cb.bdqn.gulinall.ware.vo.FareVo;
import cn.bdqn.gulimall.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:20:00
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo free(Long attrId);
}

