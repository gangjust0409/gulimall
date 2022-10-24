package cb.bdqn.gulinall.ware.service.impl;

import cb.bdqn.gulinall.ware.dao.WareInfoDao;
import cb.bdqn.gulinall.ware.entity.WareInfoEntity;
import cb.bdqn.gulinall.ware.feign.MemberAddressFeignService;
import cb.bdqn.gulinall.ware.service.WareInfoService;
import cb.bdqn.gulinall.ware.vo.FareVo;
import cb.bdqn.gulinall.ware.vo.MemberAddressVo;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.common.utils.R;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberAddressFeignService memberAddressFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("id", key).or().like("name", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo free(Long attrId) {
        // 获取地址  可以调用不同店铺的运费价格
        FareVo fareVo = new FareVo();
        R r = memberAddressFeignService.attrInfo(attrId);
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            // 用手机号的末位数来作为运费
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(substring));
            fareVo.setAddress(data);
            return fareVo;
        }

        return null;
    }

}