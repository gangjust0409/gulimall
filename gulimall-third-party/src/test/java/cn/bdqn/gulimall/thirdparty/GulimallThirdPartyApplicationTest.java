package cn.bdqn.gulimall.thirdparty;

import cn.bdqn.gulimall.thirdparty.component.SmsComponent;
import cn.bdqn.gulimall.thirdparty.utils.HttpUtils;
import com.aliyun.oss.OSS;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTest {



    @Autowired
    private OSS oss;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSend(){
        //smsComponent.sendCode02("19907869342","56214");   必须是 key:val  形式的才不会报错
        smsComponent.sendCode("19907869342","code:2563");
    }

    @Test
    public void sendCode(){
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "0c9de66de3a84416b9400a9dbdddb202";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code：44562");
        bodys.put("phone_number", "19907869342");
        bodys.put("template_id", "TPL_0000");


        try {

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendCode2() throws IOException {
        String url = "https://smssend.shumaidata.com/sms/send";
        String appCode = "0c9de66de3a84416b9400a9dbdddb202";

        Map<String, String> params = new HashMap<>();
        params.put("receive", "19907869342");
        params.put("tag", "5541254");
        params.put("templateId", "MD8B7116BC");

        String result = postForm(appCode, url, params);
        System.out.println(result);
    }

    public static String postForm(String appCode, String url, Map<String, String> params) throws IOException {
        url = url + buildRequestUrl(params);
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            formbuilder.add(key, params.get(key));
        }
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE " + appCode).post(body).build();
        Response response = client.newCall(request).execute();
        System.out.println("返回状态码" + response.code() + ",message:" + response.message());
        String result = response.body().string();
        return result;
    }

    public static String buildRequestUrl(Map<String, String> params) {
        StringBuilder url = new StringBuilder("?");
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            url.append(key).append("=").append(params.get(key)).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
    }


    @Test
    public void testUploadAli() throws FileNotFoundException {
        String content = "Hello";
        oss.putObject("gulimall-buck-just", "lei.jpg", new FileInputStream("E:\\gulimall\\project\\sources\\Guli Mall\\课件和文档(老版)\\基础篇\\资料\\pics\\d511faab82abb34b.jpg"));
        System.out.println("上传文件成功");
    }


}
