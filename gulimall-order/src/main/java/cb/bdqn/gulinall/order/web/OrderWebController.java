package cb.bdqn.gulinall.order.web;

import cb.bdqn.gulinall.order.service.OrderService;
import cb.bdqn.gulinall.order.vo.OrderConfirmVo;
import cb.bdqn.gulinall.order.vo.OrderSubmitVo;
import cb.bdqn.gulinall.order.vo.SubmitOrderResponseVo;
import cn.bdqn.gulimall.exection.NoHasStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    // 提交订单
    @PostMapping("/submit-order")
    public String toOrderSubmit(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        SubmitOrderResponseVo response = null;
        String msg = "下单失败：";
        try {
            response = orderService.submitOrder(vo);
            if (response.getCode() == 0) {
                // 下单完成 来到支付
                model.addAttribute("submitOrderResponseVo", response);
                return "pay";
            } else {
                // 下单失败：返回确认详情页
                switch (response.getCode()) {
                    case 1:
                        msg += "请重新刷新页面！";
                        break;
                    case 2:
                        msg += "验价失败！";
                        break;
                    default:
                        //case 3: msg+="库存失败！"; break;
                }
                System.out.println("结果：" + response.getCode() + "\t" + msg);
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.mmf.asia/toTrade";
            }
        } catch (NullPointerException | NoHasStockException e){
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            System.out.println("结果：" + response + "\t" + msg+e.getMessage());
            return "redirect:http://order.gulimall.mmf.asia/toTrade";
        }

    }

}
