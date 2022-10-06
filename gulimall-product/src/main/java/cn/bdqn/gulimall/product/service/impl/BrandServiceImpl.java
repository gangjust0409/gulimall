package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.product.dao.BrandDao;
import cn.bdqn.gulimall.product.entity.BrandEntity;
import cn.bdqn.gulimall.product.service.BrandService;
import cn.bdqn.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");

        if (!StringUtils.isNullOrEmpty(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }


        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateDetil(BrandEntity brand) {
        // 先把自己更新
        this.updateById(brand);

        // 在把关联的更新
        log.info(brand.getName() + "\\\\\\\\\\\\\\\\");
        if (! StringUtils.isNullOrEmpty(brand.getName())) {
            categoryBrandRelationService.findUpdateBrand(brand.getBrandId(), brand.getName());
        }

        //TODD 其他待更新

    }

    @Override
    public List<BrandEntity> getBrandsByIds(List<Long> brandIds) {
        return  baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id",brandIds));
    }

}