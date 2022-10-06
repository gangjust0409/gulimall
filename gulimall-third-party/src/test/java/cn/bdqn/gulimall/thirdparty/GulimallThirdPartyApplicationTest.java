package cn.bdqn.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTest {



    @Autowired
    private OSS oss;

    @Test
    public void testUploadAli() throws FileNotFoundException {
        String content = "Hello";
        oss.putObject("gulimall-buck-just", "lei.jpg", new FileInputStream("E:\\gulimall\\project\\sources\\Guli Mall\\课件和文档(老版)\\基础篇\\资料\\pics\\d511faab82abb34b.jpg"));
        System.out.println("上传文件成功");
    }


}
