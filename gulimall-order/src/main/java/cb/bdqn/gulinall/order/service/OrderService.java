package cb.bdqn.gulinall.order.service;

import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.vo.*;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.to.mq.OrderTo;
import cn.bdqn.gulimall.to.mq.SeckillOrderTo;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author just
 * @email just@gmail.com
 * @date 2022-04-23 14:12:17
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 结算页的返回结果
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 返回到支付页的结果
     * @param vo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderState(String orderSn);

    void closeOrder(OrderTo order);

    PayVo payOrder(String orderSn);

    PageUtils queryOrders(Map<String, Object> params);

    /**
     * 返回支付成功修改订单状态
     * @param vo
     * @return
     */
    String handlerPayResult(PayAsyncVo vo);

    String closeZFB(String orderSn) throws AlipayApiException, UnsupportedEncodingException;

    /**
     * 秒杀系统快速处理订单
     * @param to
     */
    void quickOrder(SeckillOrderTo to);
}

