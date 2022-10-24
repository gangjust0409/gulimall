package cn.bdqn.gulimall.thirdparty.component;

import cn.bdqn.gulimall.thirdparty.utils.HttpUtils;
import lombok.Data;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.cloud.alicloud")
@Data
@Component
public class SmsComponent {

    private String host;
    private String path;
    private String method;
    private String appcode;

    private String url;
    private String templateId;


    public void sendCode(String phone, String codeContent) {
        /*String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "0c9de66de3a84416b9400a9dbdddb202";*/
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:"+codeContent);
        bodys.put("phone_number", phone);
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

    public void sendCode02(String phone, String tag) {
        /*String url = "https://smssend.shumaidata.com/sms/send";
        String appCode = "0c9de66de3a84416b9400a9dbdddb202";*/

        Map<String, String> params = new HashMap<>();
        params.put("receive", phone);
        params.put("tag", tag);
        params.put("templateId", "MD8B7116BC");

        String result = null;
        try {
            result = postForm(appcode, url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
