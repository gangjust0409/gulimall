package cn.bdqn.cart.service.impl;

import cn.bdqn.cart.feign.SkuInfoFeignService;
import cn.bdqn.cart.interceptor.GulimallCartInterceptor;
import cn.bdqn.cart.service.CartService;
import cn.bdqn.cart.to.UserInfoTo;
import cn.bdqn.cart.vo.Cart;
import cn.bdqn.cart.vo.CartItem;
import cn.bdqn.cart.vo.SkuInfoVo;
import cn.bdqn.gulimall.common.utils.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.just.utils.JavaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkuInfoFeignService skuInfoFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    private final String CART_CACHE_PREFIX="gulimall:cart:";

    @Override
    public CartItem addCart(Long skuId, Integer count) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> ops = getHashMap();

        // 判断redis中是否存在当前的商品
        String itemJson = (String) ops.get(skuId.toString());
        if (itemJson == null) {
            // 把新商品存入购物车
            CartItem cartItem = new CartItem();

            // 使用异步编排
            // 远程调用查询sku的信息
            CompletableFuture<Void> getSkuFuture = CompletableFuture.runAsync(() -> {
                R r = skuInfoFeignService.getSkuInfo(skuId);
                if (r.getCode() == 0) {
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setChecked(true);
                    cartItem.setCount(count);
                    cartItem.setSkuId(skuId);
                    cartItem.setDefaultImg(skuInfo.getSkuDefaultImg());
                    cartItem.setTitle(skuInfo.getSkuTitle());
                    cartItem.setPrice(skuInfo.getPrice());
                }
            }, executor);

            // 远程调用组合属性的信息
            CompletableFuture<Void> getSkuAttrFuture = CompletableFuture.runAsync(() -> {
                List<String> skuAttr = skuInfoFeignService.getSkuAttr(skuId);
                cartItem.setSkuAttr(skuAttr);

            }, executor);


            // 两个都必须完成才返回
            CompletableFuture.allOf(getSkuFuture,getSkuAttrFuture).get();


            // 保存进 redis
            // at org.springframework.data.redis.serializer.StringRedisSerializer.serialize(StringRedisSerializer.java:36)
            // 必须序列化
            String json = JSON.toJSONString(cartItem);
            ops.put(skuId.toString(), json);

            return cartItem;
        } else {
            CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
            // 原来的数量+现在的数量
            cartItem.setCount(cartItem.getCount()+count);
            // 再存入 redis
            ops.put(skuId.toString(),JSON.toJSONString(cartItem));

            return cartItem;
        }

    }

    @Override
    public CartItem getCartBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getHashMap();
        String json = (String) ops.get(skuId.toString());
        // 转换
        CartItem cartItem = JSON.parseObject(json, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart carts() throws ExecutionException, InterruptedException {
        // 判断是否登录
        UserInfoTo userInfoTo = GulimallCartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            String uKey = CART_CACHE_PREFIX + userInfoTo.getUserId();
            // 已登录
            // 查询临时用户添加的购物车信息
            String tmpKey = CART_CACHE_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempList = this.getCartItems(tmpKey);
            if(JavaUtils.listIsNull(tempList)){
                // 和登录账号合并
                for (CartItem temp : tempList) {
                    this.addCart(temp.getSkuId(), temp.getCount());
                }
                //删除临时用户的购物车信息
                this.clearKey(tmpKey);
            }
            // 在查询当前的账号的购物车信息（包括合并后的购物车信息）
            List<CartItem> cartItems = this.getCartItems(uKey);
            cart.setItems(cartItems);
        } else {
            // 未登录
            String cartKey = CART_CACHE_PREFIX + userInfoTo.getUserKey();
            // 直接展示
            List<CartItem> cartItems = this.getCartItems(cartKey);
            cart.setItems(cartItems);
        }

        return cart;
    }

    @Override
    public void clearKey(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkCartItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> ops = getHashMap();
        CartItem cartItem = this.getCartBySkuId(skuId);
        cartItem.setChecked(check==1?true:false);
        String json = JSON.toJSONString(cartItem);

        ops.put(skuId.toString(),json);
    }

    @Override
    public void changeItemCount(Long skuId, Integer count) {
        CartItem cartItem = this.getCartBySkuId(skuId);
        cartItem.setCount(count);

        BoundHashOperations<String, Object, Object> ops = getHashMap();
        ops.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getHashMap();
        ops.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentCartItems() {
        UserInfoTo userInfoTo = GulimallCartInterceptor.threadLocal.get();
        if (userInfoTo == null) {
            return null;
        }
        String cacheKey = CART_CACHE_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = this.getCartItems(cacheKey);
        // 过滤掉没有勾选的
        List<CartItem> collect = cartItems.stream()
                // 价格必须是最新的
                .map(item -> {
                    // 远程查询最新价格
                    BigDecimal newPrice = skuInfoFeignService.getNewPrice(item.getSkuId());
                    item.setPrice(newPrice);
                    return item;
                })
                .filter(item -> item.getChecked())
                .collect(Collectors.toList());

        return collect;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
        List<Object> values = ops.values();
        if (JavaUtils.listIsNull(values)) {
            List<CartItem> collect = values.stream().map(val -> {
                String s = (String) val;
                CartItem cartItem = JSON.parseObject(s, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 封装 hash，获取key
     * @return
     */
    private BoundHashOperations<String, Object, Object> getHashMap() {
        UserInfoTo userInfoTo = GulimallCartInterceptor.threadLocal.get();
        // 封装key，如果登录用用户id作为key，没有登录以临时用户的user-key 登录
        String key = "";
        // 判断是否登录
        if (userInfoTo.getUserId() != null) {
            // 登陆了
            key = CART_CACHE_PREFIX + userInfoTo.getUserId();
        } else {
            // 未登录
            key = CART_CACHE_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        return hashOps;
    }
}
