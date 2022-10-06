package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.product.dao.AttrAttrgroupRelationDao;
import cn.bdqn.gulimall.product.dao.AttrDao;
import cn.bdqn.gulimall.product.dao.AttrGroupDao;
import cn.bdqn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import cn.bdqn.gulimall.product.entity.AttrEntity;
import cn.bdqn.gulimall.product.entity.AttrGroupEntity;
import cn.bdqn.gulimall.product.service.AttrAttrgroupRelationService;
import cn.bdqn.gulimall.product.service.AttrGroupService;
import cn.bdqn.gulimall.product.service.AttrService;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.product.vo.AttrGroupWithAttrsVo;
import cn.bdqn.gulimall.product.vo.AttrRelationVo;
import cn.bdqn.gulimall.product.vo.SpuItemBaseAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationService relationService;

    @Autowired
    private AttrService attrService;

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        // 点击三级分类时
        //select * from pms_attr_group where catelog_id and (attr_group_id= or attr_group_name=)
        if (!StringUtils.isNullOrEmpty(key)) {
            wrapper.and(item -> {
                item.eq("attr_group_id", key).or().like("attr_group_name", key);
            });

        }
        // 当是第一页的时候wrapper
        if (categoryId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", categoryId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrEntity> attrRelation(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrGroupId = relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> longList = attrGroupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // 根据id查询所有信息
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(longList);


        return attrEntities;
    }

    @Override
    public void deleteRelation(AttrRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchAttrRelation(relationEntities);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCatelogId(Long catelogId) {
        // 先查出所有分组信息
        List<AttrGroupEntity> groupEntities = this.baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 在查出每个分组下的属性信息
        List<AttrGroupWithAttrsVo> collect = groupEntities.stream().map(attrGroup -> {
            AttrGroupWithAttrsVo groupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroup, groupWithAttrsVo);
            List<AttrEntity> attrs = this.attrRelation(groupWithAttrsVo.getAttrGroupId());
            groupWithAttrsVo.setAttrs(attrs);
            return groupWithAttrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SpuItemBaseAttrVo> getAttrGroupWithBySpuId(Long spuId, Long catalogId) {

        return baseMapper.getAttrGroupWithBySpuId(spuId,catalogId);
    }


}