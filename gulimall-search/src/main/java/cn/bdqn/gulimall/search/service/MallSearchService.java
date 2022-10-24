package cn.bdqn.gulimall.search.service;

import cn.bdqn.gulimall.search.vo.SearchParam;
import cn.bdqn.gulimall.search.vo.SearchResult;

public interface MallSearchService {

    SearchResult search(SearchParam param);
}
