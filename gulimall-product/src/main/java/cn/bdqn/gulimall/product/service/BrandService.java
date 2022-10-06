package cn.bdqn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetil(BrandEntity brand);

    List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

