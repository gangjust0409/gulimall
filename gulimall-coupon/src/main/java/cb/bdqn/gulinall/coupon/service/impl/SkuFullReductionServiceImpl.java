package cb.bdqn.gulinall.coupon.service.impl;

import cb.bdqn.gulinall.coupon.entity.MemberPriceEntity;
import cb.bdqn.gulinall.coupon.entity.SkuLadderEntity;
import cb.bdqn.gulinall.coupon.service.MemberPriceService;
import cb.bdqn.gulinall.coupon.service.SkuLadderService;
import cn.bdqn.gulimall.to.MemberPrice;
import cn.bdqn.gulimall.to.SkuReductioinTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;

import cb.bdqn.gulinall.coupon.dao.SkuFullReductionDao;
import cb.bdqn.gulinall.coupon.entity.SkuFullReductionEntity;
import cb.bdqn.gulinall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductioinTo skuReductioinTo) {
        // sms-sku-loader
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductioinTo, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductioinTo.getCountStatus());
        // 必须大于1
        if (skuLadderEntity.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        // sms-sku-full-reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuFullReductionEntity, skuReductioinTo);
        skuFullReductionEntity.setAddOther(skuReductioinTo.getCountStatus());
        if (skuReductioinTo.getFullPrice() != null && skuReductioinTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.save(skuFullReductionEntity);
        }

        // sms-member-price
        List<MemberPrice> memberPrices = skuReductioinTo.getMemberPrices();
        if (memberPrices != null) {
            List<MemberPriceEntity> collect = memberPrices.stream().map(member -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductioinTo.getSkuId());
                memberPriceEntity.setMemberLevelId(member.getId());
                memberPriceEntity.setMemberLevelName(member.getName());
                memberPriceEntity.setAddOther(1);
                memberPriceEntity.setMemberPrice(member.getPrice());
                return memberPriceEntity;
            }).filter(item -> item.getMemberPrice().compareTo(new BigDecimal("0")) == 1).collect(Collectors.toList());

            // 批量保存
            memberPriceService.saveBatch(collect);
        }

    }

}