package cn.bdqn.gulimall.member.service.impl;

import cn.bdqn.gulimall.common.utils.HttpUtils;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.member.dao.MemberDao;
import cn.bdqn.gulimall.member.dao.MemberLevelDao;
import cn.bdqn.gulimall.member.entity.MemberEntity;
import cn.bdqn.gulimall.member.entity.MemberLevelEntity;
import cn.bdqn.gulimall.member.exception.PhoneExistsException;
import cn.bdqn.gulimall.member.exception.UsernameExistsException;
import cn.bdqn.gulimall.member.service.MemberService;
import cn.bdqn.gulimall.member.vo.MemberLoginVo;
import cn.bdqn.gulimall.member.vo.MemberRegistVo;
import cn.bdqn.gulimall.member.vo.SocialUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) throws UsernameExistsException,PhoneExistsException {
        // 设置用户会员信息
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultStatus();
        // 普通会员默认值
        memberEntity.setLevelId(memberLevelEntity.getId());

        // 采用异常机制来判断手机号和用户名的唯一性，从上往下执行，如果手机号和用户名已存在，那么报错，就捕获异常
        checkUsernameExists(vo.getUserName());
        checkPhoneExists(vo.getPhone());

        // 设置手机号和用户名
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setNickname(vo.getUserName());

        // 使用spring家的md5加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 加密密码
        String cryptPassword = passwordEncoder.encode(vo.getPassword());

        // 设置密码
        memberEntity.setPassword(cryptPassword);

        // 添加方法
        baseMapper.insert(memberEntity);
        log.info("注册成功！");
    }

    @Override
    public void checkUsernameExists(String userName) throws UsernameExistsException {
        // 查询数据库是否存在
        Long count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UsernameExistsException();
        }
    }

    @Override
    public void checkPhoneExists(String phone) throws PhoneExistsException {
        // 查询数据库是否存在
        Long count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistsException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String editPassword = vo.getPassword();
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (entity == null) {
            return null;
        } else {
            // 查询的不为空，在进行密码匹配是否正确
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(editPassword, entity.getPassword());
            System.out.println("err" + editPassword + "\t" + entity.getPassword() + "\t" + matches);
            if (matches) {
                // 登录成功
                return entity;
            } else {
                return null;
            }

        }
    }

    @Override
    public MemberEntity login(SocialUser user) throws Exception {
        // 判断数据库中是否已登录过
        String uid = user.getUid();
        // 查询数据库中是否存在当前登录的用户
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (entity == null) {
            // 没有就添加一个当前系统的用户
            // 查询社交登录的用户信息
            Map<String, String> map = new HashMap<>();
            map.put("access_token", user.getAccessToken());
            map.put("uid", user.getUid());

            MemberEntity memberEntity = new MemberEntity();
            HttpResponse res = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), map);

            // 解析 返回结果
            if (res.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(res.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = (String) jsonObject.get("name");
                String gender = (String) jsonObject.get("gender");
                String profileImageUrl = (String) jsonObject.get("profile_image_url");

                // 添加进入数据库
                memberEntity.setUsername(name);
                memberEntity.setNickname(name);
                memberEntity.setGender(gender.equalsIgnoreCase("m")?1:0);
                memberEntity.setHeader(profileImageUrl);


            }
            memberEntity.setAccessToken(user.getAccessToken());
            memberEntity.setSocialUid(user.getUid());
            memberEntity.setExpiresIn(user.getExpiresIn());

            baseMapper.insert(memberEntity);

            return memberEntity;



        } else {
            // 有就修改令牌和过期时间
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(entity.getAccessToken());
            memberEntity.setExpiresIn(entity.getExpiresIn());
            // 修改
            baseMapper.updateById(memberEntity);
            entity.setAccessToken(user.getAccessToken());
            entity.setExpiresIn(user.getExpiresIn());

            return entity;
        }
    }

}