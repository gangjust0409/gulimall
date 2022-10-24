package cn.bdqn.gulimall.search.controller;

import cn.bdqn.gulimall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import cn.bdqn.gulimall.search.vo.SearchParam;
import cn.bdqn.gulimall.search.vo.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchControllerWeb {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String searchList(SearchParam param, Model model, HttpServletRequest request) {
        param.setUrlQuery(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);

        return "list";
    }


}
