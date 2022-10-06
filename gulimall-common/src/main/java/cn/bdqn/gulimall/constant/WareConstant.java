package cn.bdqn.gulimall.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/5/5
 */
public class WareConstant {

    @Getter
    @AllArgsConstructor
    public enum PurchaseStatusEnum {

        CREATED(0,"创建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISHED(3, "已完成"),
        HASERRO(4, "有异常"),;



        private int code;
        private String msg;

    }

    @Getter
    @AllArgsConstructor
    public enum PurchaseDetailStatusEnum {

        CREATED(0,"创建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISHED(3, "已完成"),
        HASERRO(4, "采购失败"),;



        private int code;
        private String msg;

    }

}
