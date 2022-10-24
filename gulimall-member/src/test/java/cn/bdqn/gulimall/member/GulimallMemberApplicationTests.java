package cn.bdqn.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {

        // md 5 加密
        String s = DigestUtils.md5Hex("1234567");
        // fcea920f7412b5da7be0cf42b8c93759
        // 彩虹表  会被爆破密码
        //System.out.println(s);
        // 加盐，盐值加密   加上一个随机值，和密码一起加密，然后数据库另外加一个字段来保存该随机值
        String yan = UUID.randomUUID().toString().substring(0, 6);
        // $1$f96cec$iS6XjxwcgC5hDV8fnq5iH/
        // $1$de1a35$n5VNzrEGq5CANhARdlXt/.
        String crypt = Md5Crypt.md5Crypt("123456789".getBytes(), "$1$"+yan);
        //System.out.println(crypt);
        // 使用 spring 家的加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // $2a$10$k6pNNkktplVmao5k732yuO8xUIHpY8Whzpc6.mya8aEus5LsAHgnq
        String encode = passwordEncoder.encode("123456789");

        // 匹配铭文密码和密文密码是否相同
        boolean matches = passwordEncoder.matches("123456789", encode);
        System.out.println(encode+"=>"+matches);
        // $2a$10$2Jz8/RrPqeDdWIzouXF1W.Oc3OUyGA0pxANZXr6SdhP7DvPqhwCQm=>true 第一次测试结果
        // $2a$10$lYRrIXmfLUKZHxmCZJRVIOTqzCSkH9gFWaKiiCcMY.NgHsDAy9w8.=>true 第二次测试结果
    }

}
