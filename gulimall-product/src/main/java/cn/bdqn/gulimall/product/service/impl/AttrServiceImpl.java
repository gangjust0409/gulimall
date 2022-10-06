package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.constant.AttrConstant;
import cn.bdqn.gulimall.product.dao.AttrDao;
import cn.bdqn.gulimall.product.dao.AttrGroupDao;
import cn.bdqn.gulimall.product.dao.CategoryDao;
import cn.bdqn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import cn.bdqn.gulimall.product.entity.AttrEntity;
import cn.bdqn.gulimall.product.entity.AttrGroupEntity;
import cn.bdqn.gulimall.product.entity.CategoryEntity;
import cn.bdqn.gulimall.product.service.AttrAttrgroupRelationService;
import cn.bdqn.gulimall.product.service.AttrService;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.product.vo.AttrGoupVO;
import cn.bdqn.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveGroup(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        // 把页面的数据赋给实体类对象，保存基本数据
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        if (attrEntity.getAttrType() == AttrConstant.ProductAttrType.BASE_TYPE_ATTR.getCode() && attr.getAttrGroupId() != null) {
            //保存关联联系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
        log.info("保存成功！");
    }

    @Override
    public PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", attrType.equalsIgnoreCase("base")?
                        AttrConstant.ProductAttrType.BASE_TYPE_ATTR.getCode():
                        AttrConstant.ProductAttrType.SALE_TYPE_ATTR.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);

        List<AttrGoupVO> goupVOList = page.getRecords().stream().map(ae -> {
            AttrGoupVO attrGoupVO = new AttrGoupVO();

            // 拷贝
            BeanUtils.copyProperties(ae, attrGoupVO);
            // 是基本信息才查出分组名称
            log.info(attrType);
            if ("base".equalsIgnoreCase(attrType)) {
                // 查出分组名称
                log.error(ae.getAttrId() + "][");
                AttrAttrgroupRelationEntity attr_id =
                        attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", ae.getAttrId()));
                if (attr_id != null && attr_id.getAttrGroupId() != null && attr_id.getAttrGroupId() != 0) {
                    attrGoupVO.setAttrGroupId(attr_id.getAttrGroupId());
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attr_id.getAttrGroupId());
                    attrGoupVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            // 查出分类名称
            if (ae.getCatelogId() != null || ae.getCatelogId() != 0) {
                CategoryEntity categoryEntity = categoryDao.selectById(ae.getCatelogId());
                attrGoupVO.setCatelogName(categoryEntity.getName());
            }
            return attrGoupVO;
        }).collect(Collectors.toList());

        pageUtils.setList(goupVOList);

        return pageUtils;
    }

    @Cacheable(value = "attr", key = "'attrInfo：'+#root.args[0]")
    @Override
    public AttrGoupVO getAttrInfoVo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrGoupVO attrGoupVO = new AttrGoupVO();
        BeanUtils.copyProperties(attrEntity, attrGoupVO);

        if (attrEntity.getAttrType() == AttrConstant.ProductAttrType.BASE_TYPE_ATTR.getCode()) {
            // 查出分组名称
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity!=null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrGoupVO.setAttrGroupId(relationEntity.getAttrGroupId());
                    attrGoupVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 查出分类名称
        Long[] londPath = categoryService.findAttrGroupLondPath(attrEntity.getCatelogId());
        if (londPath != null) {
            attrGoupVO.setCatelogPath(londPath);
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity!=null) {
                attrGoupVO.setCatelogName(categoryEntity.getName());
            }
        }

        return attrGoupVO;
    }

    @Override
    public void updateAttr(AttrGoupVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 保存基本信息
        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == AttrConstant.ProductAttrType.BASE_TYPE_ATTR.getCode()) {
            // 保存分组信息
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());


            long count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            if (count > 0) {
                // 修改
                attrAttrgroupRelationService.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                // z则是分组id为空，重新添加
                attrAttrgroupRelationService.save(relationEntity);
            }

        }
    }

    @Override
    public PageUtils queryNoAttrRelation(Map<String, Object> params, Long attrgroupId) {
        // 当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 当前分组只能关联别的分组没有引用的属性
        //      当前分类下的其他分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> longList = group.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //      这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", longList));
        List<Long> ids = groupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        //      从当前分类中移除这些属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", AttrConstant.ProductAttrType.BASE_TYPE_ATTR.getCode());
        if (ids != null && ids.size() > 0) {
            attrEntityQueryWrapper.notIn("attr_id", ids);
        }
        // 模糊查询
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)) {
            attrEntityQueryWrapper.and(wapper -> {
                wapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);

        PageUtils pageUtils = new PageUtils(page);


        return pageUtils;
    }

    @Override
    public List<Long> selectSearchAttrId(Collection<Long> ids) {
        // select attr_id from pms_attr where in () and search_type = 1
        return baseMapper.selectSearchAttrId(ids);
    }


}