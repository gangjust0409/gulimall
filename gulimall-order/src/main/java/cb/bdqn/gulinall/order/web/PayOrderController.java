package cb.bdqn.gulinall.order.web;

import cb.bdqn.gulinall.order.config.AlipayTemplate;
import cb.bdqn.gulinall.order.service.OrderService;
import cb.bdqn.gulinall.order.vo.PayVo;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayOrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 因为返回的是html文件，所以直接返回
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder/{orderSn}.html", produces = "text/html")
    public String payOrder(@PathVariable String orderSn) throws AlipayApiException {
        // 封装数据
        PayVo payVo = orderService.payOrder(orderSn);
        String pay = alipayTemplate.pay(payVo);// 执行支付的方法
        return pay;
    }

}
