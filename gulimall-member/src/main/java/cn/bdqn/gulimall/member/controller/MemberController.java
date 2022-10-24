package cn.bdqn.gulimall.member.controller;

import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import cn.bdqn.gulimall.member.entity.MemberEntity;
import cn.bdqn.gulimall.member.exception.PhoneExistsException;
import cn.bdqn.gulimall.member.exception.UsernameExistsException;
import cn.bdqn.gulimall.member.service.MemberService;
import cn.bdqn.gulimall.member.vo.MemberLoginVo;
import cn.bdqn.gulimall.member.vo.MemberRegistVo;
import cn.bdqn.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * 会员
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-05-03 09:12:43
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/auth/login")
    public R oauthLogin(@RequestBody SocialUser user) throws Exception {
        MemberEntity entity = memberService.login(user);

        if (entity==null){
            return R.error(BizExceptionCode.LOGIN_MEMBER_INVALID_EXCEPTION.getCode(), BizExceptionCode.LOGIN_MEMBER_INVALID_EXCEPTION.getMsg());
        } else {
            return R.ok().setData(entity);
        }
    }


    // 登录
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        MemberEntity entity = memberService.login(vo);
        if (entity==null){
            return R.error(BizExceptionCode.LOGIN_MEMBER_INVALID_EXCEPTION.getCode(), BizExceptionCode.LOGIN_MEMBER_INVALID_EXCEPTION.getMsg());
        } else {
            return R.ok().setData(entity);
        }
    }

    // 注册
    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo){

        try{
            memberService.regist(vo);
        } catch (UsernameExistsException e) {
            // 要判断是什么类型的异常
            return R.error(BizExceptionCode.USERNAME_EXISTS_EXCEPTION.getCode(),BizExceptionCode.USERNAME_EXISTS_EXCEPTION.getMsg());
        } catch (PhoneExistsException e) {
            return R.error(BizExceptionCode.PHONE_EXISTS_EXCEPTION.getCode(), BizExceptionCode.PHONE_EXISTS_EXCEPTION.getMsg());
        }

        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
