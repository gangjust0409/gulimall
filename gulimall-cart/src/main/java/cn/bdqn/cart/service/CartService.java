package cn.bdqn.cart.service;

import cn.bdqn.cart.vo.Cart;
import cn.bdqn.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    // 添加到购物车
    CartItem addCart(Long skuId, Integer count) throws ExecutionException, InterruptedException;

    // 购物车的某项信息
    CartItem getCartBySkuId(Long skuId);

    // 查询所有的购物车信息
    Cart carts() throws ExecutionException, InterruptedException;

    // 删除当前key的所有values
    void clearKey(String cartKey);

    // 修改购物车勾选商品的状态
    void checkCartItem(Long skuId, Integer check);

    // 修改购物车商品数量
    void changeItemCount(Long skuId, Integer count);

    // 删除购物车某项商品
    void deleteItem(Long skuId);

    List<CartItem> getCurrentCartItems();
}
