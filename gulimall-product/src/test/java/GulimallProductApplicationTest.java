//import com.aliyun.oss.*;

import cn.bdqn.gulimall.product.dao.AttrGroupDao;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.product.vo.SpuItemBaseAttrVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class GulimallProductApplicationTest {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CategoryService categoryService;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    private RedissonClient redissonClient;

    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void categoryTest() {
        Long[] attrGroupLondPath = categoryService.findAttrGroupLondPath(255L);
    }

    //@Autowired
   //OSS oss;

    @Test
    public void test() {
        List<SpuItemBaseAttrVo> attrGroupWithBySpuId = attrGroupDao.getAttrGroupWithBySpuId(13L, 225L);
        System.out.println(attrGroupWithBySpuId);
    }

    // 测试 redis
    @Test
    public void redisTest() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 设置
        ops.set("hello", "word" + UUID.randomUUID());
        // 读取
        String hello = ops.get("hello");
        System.out.println(hello);
        System.out.println("ss");
    }

//    public static void main(String[] args) {
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        // 设置
//        ops.set("hello", "word" + UUID.randomUUID());
//        // 读取
//        String hello = ops.get("hello");
//        System.out.println(hello);
//    }

    /*@Test
    public void uploadFile() throws FileNotFoundException {

        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5t7N5DEqnsD94BrqAgmf";
        String accessKeySecret = "iDI7aUgOFFgucAFgWQTYqLYg9pwKjy";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "gulimall-gang-just";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String objectName =  format + "/";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String content = "Hello OSS";
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

            FileInputStream fileInputStream = new FileInputStream("F:\\my\\mys\\52191090810932343341642990732575.jpg");

            ossClient.putObject("gulimall-gang-just", "e2.jpg", fileInputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        System.out.println("文件上传成功！");
    }
*/



}
