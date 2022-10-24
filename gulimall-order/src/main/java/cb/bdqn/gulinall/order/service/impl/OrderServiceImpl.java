package cb.bdqn.gulinall.order.service.impl;

import cb.bdqn.gulinall.order.config.AlipayTemplate;
import cb.bdqn.gulinall.order.constant.OrderConstant;
import cb.bdqn.gulinall.order.dao.OrderDao;
import cb.bdqn.gulinall.order.entity.OrderEntity;
import cb.bdqn.gulinall.order.entity.OrderItemEntity;
import cb.bdqn.gulinall.order.entity.PaymentInfoEntity;
import cb.bdqn.gulinall.order.enume.OrderStatusEnum;
import cb.bdqn.gulinall.order.feign.CartFeignService;
import cb.bdqn.gulinall.order.feign.MemberAddressFeignService;
import cb.bdqn.gulinall.order.feign.ProductFeignService;
import cb.bdqn.gulinall.order.feign.WareFeignService;
import cb.bdqn.gulinall.order.interceptor.OrderLoginUserInterceptor;
import cb.bdqn.gulinall.order.service.OrderItemService;
import cb.bdqn.gulinall.order.service.OrderService;
import cb.bdqn.gulinall.order.service.PaymentInfoService;
import cb.bdqn.gulinall.order.to.CreateOrderTo;
import cb.bdqn.gulinall.order.vo.*;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;
import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.NoHasStockException;
import cn.bdqn.gulimall.to.mq.OrderTo;
import cn.bdqn.gulimall.to.mq.SeckillOrderTo;
import cn.bdqn.gulimall.vo.MemberVo;
import cn.bdqn.gulimall.vo.OrderSpuInfoVo;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private MemberAddressFeignService memberAddressFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AlipayTemplate alipayTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 主线程100
     * cart线程133
     * address线程132
     * requestInterceptor线程133
     * requestInterceptor线程132
     * 异步导致RequestInterceptor获取request为null
     * 解决方案：
     *      两个不一样的线程，为ta赋值
     *      主线程
     *      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
     *      父线程
     *      RequestContextHolder.setRequestAttributes(requestAttributes);
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberVo memberVo = OrderLoginUserInterceptor.threadLocal.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            // 远程查询当前会员的地址
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberAddressFeignService.addressEntities(memberVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> getCartItemFuture = CompletableFuture.runAsync(() -> {
            // 远程查询购物车的信息
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItem> currentCartItems = cartFeignService.getCurrentCartItems();
            confirmVo.setItems(currentCartItems);
        }, executor).thenRunAsync(()->{
            // 批量查询库存信息
            List<OrderItem> items = confirmVo.getItems();
            List<Long> longs = items.stream().map(OrderItem::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.wareSku(longs);
            List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                // 转成map对象返回
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);


        // 可以直接在会员里有积分
        Integer integration = memberVo.getIntegration();
        confirmVo.setUseIntegration(integration);



        //TODO 放重令牌  放入到redis中
        String token = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberVo.getId(), token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFuture, getCartItemFuture).get();
        return confirmVo;
    }

    /**
     * 本地事务只能控制当前服务的数据库回滚，控不了其他服务的回滚
     * 分布式事务 最大原因。网络问题+分布式问题
     * 其他远程服务使用 @Transactional 就可以了
     * @param vo
     * @return
     */
    //@GlobalTransactional  // 大业务使用这个注解来控制事务  高并发不使用 at 模式，里面加了很多锁，和提升不了吞吐量
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        orderSubmitVoThreadLocal.set(vo);
        MemberVo memberVo = OrderLoginUserInterceptor.threadLocal.get();
        String orderToken = vo.getOrderToken(); // 得到页面的token
        response.setCode(0);
        // 验证令牌：使用lua脚本保证原子性
        String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberVo.getId()), orderToken);
        // 如果验证令牌成功返回1，失败就返回0
        if (execute == 0) {
            // 验证令牌失败
            response.setCode(1);
            return response;
        } else {
            // 验证令牌成功
            // 下单：创建订单、验令牌、验价格、锁库存
            // 创建一个订单
            CreateOrderTo order = createOrder();

            // 验价
            BigDecimal payPrice = order.getPayPrice();
            BigDecimal totalPrice = vo.getTotalPrice();
            if (Math.abs(payPrice.subtract(totalPrice).doubleValue()) < 0.01) {
                // 成功
                // 保存订单信息
                this.save(order.getOrder());
                orderItemService.insertBatch(order.getOrderItems());

                // 锁库存
                OrderWareVo orderWareVo = new OrderWareVo();
                orderWareVo.setOrderSn(order.getOrder().getOrderSn());
                // 筛选数据
                List<OrderItem> locks = order.getOrderItems().stream().map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setSkuId(item.getSkuId());
                    orderItem.setTitle(item.getSkuName());
                    orderItem.setCount(item.getSkuQuantity());
                    return orderItem;
                }).collect(Collectors.toList());
                orderWareVo.setLocks(locks);

                R r = wareFeignService.orderLock(orderWareVo);
                if (r.getCode() == 0) {
                    // 锁库成功
                    response.setOrder(order.getOrder());
                    // 远程扣减积分，出异常，订单辉光，库存不滚
                    // 模拟错误异常
                    //int i = 10/0;
                    //TODO 创建订单成功后 判断用户是否支付  发送消息的时候，服务宕机，消息可能会丢失，使用数据库创建一个专门记录发送mq的日志保存，定期保存
                    try{
                        OrderTo orderTo = new OrderTo();
                        BeanUtils.copyProperties(order.getOrder(), orderTo);
                        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order", orderTo);
                    } catch (Exception e) {
                        // 服务和mq服务器发送了网络问题，记录日志，定期扫描数据库进行发送
                    }
                    return response;
                } else {
                    // 锁库失败
                    //response.setCode(3);
                    throw  new NoHasStockException();
                    //return response;
                }

            } else {
                // 失败
                response.setCode(2);
                return response;
            }

        }
    }

    @Override
    public OrderEntity getOrderState(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderTo order) {
        // 查询最新的状态
        OrderEntity orderEntity = this.getById(order.getId());
        // 修改状态消息
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(orderEntity);
            // 给mq发送消息，防止在关闭订单前把库存释放
            rabbitTemplate.convertAndSend("order-event-exchange","order.release.order",order);
        }
    }

    @Override
    public PayVo payOrder(String orderSn) {
        PayVo payVo = new PayVo();
        // 根据订单号查询订单详情信息
        OrderEntity order = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        // 查询订单详情        保留2位小数    如果小数不是0，那么向上取整
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_CEILING);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(orderSn);
        List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity itemEntity = list.get(0); // 使用第一个商品名称作为主题和备注
        payVo.setBody(itemEntity.getSkuName()); // 备注
        payVo.setSubject(itemEntity.getSkuName()); // 主题

        // 修改订单状态
        /*order.setStatus(OrderStatusEnum.PAYED.getCode());
        this.updateById(order);*/
        //System.out.println("支付成功，修改订单状态成功！");

        return payVo;
    }

    @Override
    public PageUtils queryOrders(Map<String, Object> params) {
        MemberVo memberVo = OrderLoginUserInterceptor.threadLocal.get();
        QueryWrapper<OrderEntity> member_id = new QueryWrapper<OrderEntity>()
                .eq("member_id", memberVo.getId())
                .orderByDesc("id"); // 按照id降序排列


        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                member_id
        );

        List<OrderEntity> orders = page.getRecords().stream().map(item -> {

            // 查询每个订单的item
            List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", item.getOrderSn()));

            item.setOrderItems(order_sn);
            System.out.println(item);
            return item;
        }).collect(Collectors.toList());

        page.setRecords(orders);

        return new PageUtils(page);
    }

    @Override
    public String handlerPayResult(PayAsyncVo vo) {
        // 保存一个支付信息流水号
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        // 保存支付信息
        paymentInfoService.save(infoEntity);

        if (vo.getTrade_status().equals("TRADE_SUCCESS")) {
            // 修改订单状态
            this.baseMapper.updateOrderStatus(vo.getOut_trade_no(), OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }

    @Override
    public String closeZFB(String orderSn) throws AlipayApiException, UnsupportedEncodingException {
        AlipayClient alipayClient = new DefaultAlipayClient(alipayTemplate.getGatewayUrl(), alipayTemplate.getApp_id(), alipayTemplate.getMerchant_private_key(), "json", alipayTemplate.getCharset(), alipayTemplate.getAlipay_public_key(), alipayTemplate.getSign_type());

        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        // 根据订单号查询支付信息
        PaymentInfoEntity payInfo = paymentInfoService.getOne(new QueryWrapper<PaymentInfoEntity>().eq("order_sn", orderSn));
        //商户订单号，商户网站订单系统中唯一订单号
        String out_trade_no = new String(orderSn.getBytes("ISO-8859-1"),"UTF-8");
        ////支付宝交易号
        String trade_no = new String(payInfo.getAlipayTradeNo().getBytes("ISO-8859-1"),"UTF-8");
        //请二选一设置

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," +"\"trade_no\":\""+ trade_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        System.out.println(result);
        return null;
    }

    @Override
    public void quickOrder(SeckillOrderTo to) {
        //TODO 创建一个订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(to.getOrderSn());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setCreateTime(new Date());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setMemberId(to.getMemberId());
        //需要支付的价格
        BigDecimal multiply = to.getSeckillPrice().multiply(new BigDecimal(to.getNum().toString()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        //TODO 保存订单项信息
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(orderEntity.getOrderSn());
        orderItemEntity.setRealAmount(multiply);
        orderItemEntity.setSkuQuantity(to.getNum());

        //TODO 查询spuInfo信息
        R r = productFeignService.spuInfoBySkuId(to.getSkuId());
        if (r.getCode()==0){
            OrderSpuInfoVo data = r.getData(new TypeReference<OrderSpuInfoVo>() {
            });
            orderItemEntity.setSpuBrand(data.getBrandName());
            orderItemEntity.setSpuName(data.getSpuName());
            orderItemEntity.setSpuId(data.getId());
        }
        orderItemService.save(orderItemEntity);
        //TODO 其他信息
        System.out.println("快速创建订单成功！");
    }

    /**
     * 创建订单
     * @return
     */
    private CreateOrderTo createOrder() {
        CreateOrderTo createOrderTo = new CreateOrderTo();
        // 构建订单
        OrderEntity order = builderOrder();

        // 构建订单项
        List<OrderItemEntity> orderItems = builderOrderItems(order.getOrderSn());

        // 计算价格
        computePrice(order, orderItems);
        createOrderTo.setOrder(order);
        createOrderTo.setOrderItems(orderItems);
        createOrderTo.setPayPrice(order.getPayAmount());

        return createOrderTo;
    }

    /**
     * 计算价格
     * @param order
     * @param orderItems
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
        // 叠加
        BigDecimal total = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        BigDecimal gration = new BigDecimal("0");
        BigDecimal growth = new BigDecimal("0");

        for (OrderItemEntity item : orderItems) {
            total = total.add(item.getRealAmount());
            promotion = promotion.add(item.getPromotionAmount());
            coupon = coupon.add(item.getCouponAmount());
            integration = integration.add(item.getIntegrationAmount());
            gration = growth.add(new BigDecimal(item.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(item.getGiftGrowth().toString()));
        }
        // 计算价格
        // 总额
        order.setTotalAmount(total);
        // 应付金额
        order.setPayAmount(total.add(order.getFreightAmount()));
        // 优惠信息
        order.setPromotionAmount(promotion);
        order.setCouponAmount(coupon);
        order.setIntegrationAmount(integration);
        // 积分等信息
        order.setIntegration(gration.intValue());
        order.setGrowth(growth.intValue());

        // 默认值
        order.setDeleteStatus(0); // 0 未删除

    }


    /**
     * 构建订单信息
     * @return
     */
    private OrderEntity builderOrder() {
        OrderSubmitVo submitVo = orderSubmitVoThreadLocal.get();
        MemberVo memberVo = OrderLoginUserInterceptor.threadLocal.get();
        OrderEntity order = new OrderEntity();
        // 生成一个订单号
        String orderSn = IdWorker.getTimeId();
        order.setOrderSn(orderSn);
        // 获取当前用户的地址和运费信息
        R r = wareFeignService.jisuanFree(submitVo.getAddrId());
        FareVo data = r.getData(new TypeReference<FareVo>() {
        });
        if (data != null) {
            // 设置运费
            order.setFreightAmount(data.getFare());
            // 设置当前收货人的信息
            order.setMemberId(memberVo.getId());
            order.setReceiverName(data.getAddress().getName());
            order.setReceiverCity(data.getAddress().getCity());
            order.setReceiverProvince(data.getAddress().getProvince());
            order.setReceiverDetailAddress(data.getAddress().getDetailAddress());
            order.setReceiverPhone(data.getAddress().getPhone());
            order.setReceiverPostCode(data.getAddress().getPostCode());
            order.setReceiverRegion(data.getAddress().getRegion());
            order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
            order.setAutoConfirmDay(7); // 默认几天未收货，自动退货
            return order;
        }
        return null;
    }

    /**
     * 构建所有的订单项
     * @return
     * @param orderSn
     */
    private List<OrderItemEntity> builderOrderItems(String orderSn) {
        // 这里是获取到最后的商品价格
        List<OrderItem> currentCartItems = cartFeignService.getCurrentCartItems();
        List<OrderItemEntity> collect = currentCartItems.stream().map(cartItem -> {
            OrderItemEntity itemEntity = builderOrderItem(cartItem);
            // 设置订单号
            itemEntity.setOrderSn(orderSn);
            return itemEntity;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 构建某一个订单项
     * @return
     * @param cartItem
     */
    private OrderItemEntity builderOrderItem(OrderItem cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 订单号 √
        // spu 信息 勾
        R r = productFeignService.spuInfoBySkuId(cartItem.getSkuId());
        OrderSpuInfoVo data = r.getData(new TypeReference<OrderSpuInfoVo>() {
        });
        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuBrand(data.getBrandName());
        orderItemEntity.setSpuName(data.getSpuName());
        orderItemEntity.setCategoryId(data.getCatalogId());

        // sku 信息 √
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getDefaultImg());
        // 将集合转成字符串
        String attrs = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(attrs);
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        orderItemEntity.setSkuQuantity(cartItem.getCount());

        // 赠送积分 √
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        // 计算价格
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        // （单价*数量）-优惠价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(orderItemEntity.getPromotionAmount()).subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());

        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }

}