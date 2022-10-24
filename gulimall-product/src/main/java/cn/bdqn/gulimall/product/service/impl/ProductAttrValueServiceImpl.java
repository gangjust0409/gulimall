package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.product.dao.ProductAttrValueDao;
import cn.bdqn.gulimall.product.entity.ProductAttrValueEntity;
import cn.bdqn.gulimall.product.service.ProductAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatchAttr(List<ProductAttrValueEntity> baseAttrs) {
        this.saveBatch(baseAttrs);
    }

    @Override
    public List<ProductAttrValueEntity> baseListforspu(Long spuId) {
        List<ProductAttrValueEntity> valueEntityList = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return valueEntityList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBatchBySpuId(Long spuId, List<ProductAttrValueEntity> entity) {
        // 1 删除这个spuid之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        // 在进行修改原来的数据
        // 封装好spuid
        List<ProductAttrValueEntity> collect = entity.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        // 批量给你个信操作
        this.saveBatch(collect);
    }

}