package cn.bdqn.gulimall.product.service;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.SkuInfoEntity;
import cn.bdqn.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuById(Long spuId);

    SkuItemVo items(Long skuId) throws ExecutionException, InterruptedException;
}

