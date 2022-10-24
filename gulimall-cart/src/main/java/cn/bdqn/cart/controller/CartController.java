package cn.bdqn.cart.controller;

import cn.bdqn.cart.service.CartService;
import cn.bdqn.cart.vo.Cart;
import cn.bdqn.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/getCurrentItems")
    @ResponseBody
    public List<CartItem> getCurrentCartItems(){
        return cartService.getCurrentCartItems();
    }

    /**
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.carts();

        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * ra.addAttribute("skuId", skuId); 重定向可以拼接上 url后面的参数
     * @param skuId
     * @param count
     * @param ra
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String toAddCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("count") Integer count,
                            RedirectAttributes ra
                            //Model model
                            ) throws ExecutionException, InterruptedException {

        cartService.addCart(skuId, count);

        //model.addAttribute("item", cartItem);
        ra.addAttribute("skuId", skuId);

        return "redirect:http://cart.gulimall.mmf.asia/add-to-cart.html";
    }

    @GetMapping("/add-to-cart.html")
    public String toAddCartPage(@RequestParam("skuId") Long skuId, Model model){

        CartItem cartItem = cartService.getCartBySkuId(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

    // 修改购物车状态
    @GetMapping("/cartItemCheck")
    public String cartItemCheck(
            @RequestParam("skuId") Long skuId,
            @RequestParam("check") Integer check
                ){
        cartService.checkCartItem(skuId, check);

        return "redirect:http://cart.gulimall.mmf.asia/cart.html";
    }

    // 修改购物车商品数量
    @GetMapping("/changeItemCount")
    public String changeItemCount(@RequestParam("skuId") Long skuId, @RequestParam("count") Integer count){
        cartService.changeItemCount(skuId, count);

        return "redirect:http://cart.gulimall.mmf.asia/cart.html";
    }

    @GetMapping("/delItem")
    public String delItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.mmf.asia/cart.html";
    }

}
