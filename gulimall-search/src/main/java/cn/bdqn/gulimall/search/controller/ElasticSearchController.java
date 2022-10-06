package cn.bdqn.gulimall.search.controller;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.search.service.ProductSearchService;
import cn.bdqn.gulimall.to.es.SkuEsModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/es/save")
public class ElasticSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    // 商品上架
    @PostMapping("/product")
    public R productUp(@RequestBody List<SkuEsModule> skuEsModules) {
        boolean b = false;
        try {
            b = productSearchService.productSearchUp(skuEsModules);
        } catch (IOException e) {
            log.error("elasticsearch 商品上架错误：{}",e);
            return R.error(BizExceptionCode.PRODUCT_UP_EXCEPTION.getCode(),BizExceptionCode.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!b) { return R.ok(); }
        else { return R.error(BizExceptionCode.PRODUCT_UP_EXCEPTION.getCode(),BizExceptionCode.PRODUCT_UP_EXCEPTION.getMsg()); }
    }

}
