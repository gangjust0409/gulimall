package cb.bdqn.gulinall.order.listener;

import cb.bdqn.gulinall.order.config.AlipayTemplate;
import cb.bdqn.gulinall.order.service.OrderService;
import cb.bdqn.gulinall.order.vo.PayAsyncVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class PayNotifyListener {

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 请求参数会被封装成一个 PayAsyncVo 对象
     * @param vo
     * @param request
     * @return
     */
    @PostMapping("/pay/notify")
    public String payNotify(PayAsyncVo vo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        // 获取请求参数
        /*Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("支付宝支付成功了...的参数："+parameterMap);*/
        // 验签
        //获取支付宝POST过来反馈信息
        System.out.println("正在验签中...");
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        //TODO 先保存一个交易流水号，然后在进行修改订单状态
        System.out.println(signVerified);
        if (signVerified) {

            String res = orderService.handlerPayResult(vo);
            System.out.println("验签成功...");
            return res;
        } else {
            System.out.println("验签失败...");
            return "error";
        }
    }

}
