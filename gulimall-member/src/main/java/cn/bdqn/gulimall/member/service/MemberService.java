package cn.bdqn.gulimall.member.service;

import cn.bdqn.gulimall.member.exception.PhoneExistsException;
import cn.bdqn.gulimall.member.exception.UsernameExistsException;
import cn.bdqn.gulimall.member.vo.MemberLoginVo;
import cn.bdqn.gulimall.member.vo.MemberRegistVo;
import cn.bdqn.gulimall.member.vo.SocialUser;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-05-03 09:12:43
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkUsernameExists(String userName) throws UsernameExistsException;

    void checkPhoneExists(String phone) throws PhoneExistsException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser user) throws Exception;
}

