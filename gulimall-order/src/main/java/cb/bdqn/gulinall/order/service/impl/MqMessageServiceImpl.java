package cb.bdqn.gulinall.order.service.impl;

import cb.bdqn.gulinall.order.dao.MqMessageDao;
import cb.bdqn.gulinall.order.entity.MqMessageEntity;
import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.entity.OrderReturnReasonEntity;
import cb.bdqn.gulinall.order.service.MqMessageService;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service("mqMessageService")
//@RabbitListener(queues = {"java-queue"})
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MqMessageEntity> page = this.page(
                new Query<MqMessageEntity>().getPage(params),
                new QueryWrapper<MqMessageEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *     @RabbitListener(queues = {"","",(指定队列,数组类型)})
     *  类型：
     *      原生消息详细信息 头  + 体：Message message
     *              message.getBody() 内容
     *      T<发送消息的类型>OrderReturnReasonEntity orderReturnReasonEntity
     * Queue 可以很多人都来接收消息，只要收到消息，队列就会删除消息，而且只能有一个接受消息
     *  消费端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）
     *      1 默认时自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除这个消息
     *      问题：收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机了，发送消息丢失
     *              消费者手动确认模式，只要我们没有明确告诉mq，货物自动被签收，没有ack
     *                   消息就一直时unacked状态，即使consumer宕机，消息也不会丢失，也重新变为ready，下次会自动加入队列中
     *      如何签收
     *          channel.basicAck(deliveryTag, false); 签收成功，业务完成就签收
     *          channel.basicNack(deliveryTag,false, true); 拒签  业务失败
     *
     */
    //@RabbitHandler
    public void listenerMq(Message message,
                           OrderReturnReasonEntity orderReturnReasonEntity,
                           Channel channel
                        ){
        //System.out.println("接收消息：" + orderReturnReasonEntity.getName());/
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //System.out.println(deliveryTag);
        try {
            if (deliveryTag % 2 == 0) {

                //            channel里的自增id，是否批量处理
                channel.basicAck(deliveryTag, false);
                System.out.println("提交了"+deliveryTag);
            } else{         //channel里的自增id，是否批量处理   是否从新加入队列中，false 直接丢弃，true 重新加入队列
                channel.basicNack(deliveryTag,false, false);
                // 和以上方法 basicNack 区别就是，是否可以1批量处理
                //channel.basicReject(deliveryTag, true);
                System.out.println("未提交！"+deliveryTag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("接受消息结束！");

    }

    //@RabbitHandler
    public void listenerMq(OrderEntity orderEntity){
        System.out.println("接收消息：" + orderEntity.getReceiverName());

    }

}