package cn.bdqn.gulimall.search.service;

import cn.bdqn.gulimall.to.es.SkuEsModule;

import java.io.IOException;
import java.util.List;

public interface ProductSearchService {
    Boolean productSearchUp(List<SkuEsModule> skuEsModules) throws IOException;
}
