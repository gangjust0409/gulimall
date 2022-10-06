package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.product.vo.AttrRelationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;

import cn.bdqn.gulimall.product.dao.AttrAttrgroupRelationDao;
import cn.bdqn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import cn.bdqn.gulimall.product.service.AttrAttrgroupRelationService;

@Slf4j
@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveRelation(AttrRelationVo[] attrRelationVo) {


        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(attrRelationVo).stream().map(attr -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attr, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());


        this.saveBatch(collect);

        log.info("保存成功！");
    }

}