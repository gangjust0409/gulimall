package cn.bdqn.gulimall.search.service;

import vo.SearchParam;
import vo.SearchResult;

public interface MallSearchService {

    SearchResult search(SearchParam param);
}
