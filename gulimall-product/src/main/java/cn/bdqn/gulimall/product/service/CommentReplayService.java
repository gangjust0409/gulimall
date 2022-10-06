package cn.bdqn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

