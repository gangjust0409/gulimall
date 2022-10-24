package cb.bdqn.gulinall.ware.listener;

import cb.bdqn.gulinall.ware.service.WareSkuService;
import cn.bdqn.gulimall.to.mq.OrderTo;
import cn.bdqn.gulimall.to.mq.WareLockSkuTo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"stock.release.stock.queue"})
@Service
public class StockReleaseService {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void lockListener(WareLockSkuTo to, Message message, Channel channel) throws IOException {
        try{
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleCloseOrderWare(OrderTo order, Message message, Channel channel) throws IOException {
        try{
            wareSkuService.unLockStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
