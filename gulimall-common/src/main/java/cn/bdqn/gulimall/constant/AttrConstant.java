package cn.bdqn.gulimall.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/25
 */
public class AttrConstant {

    @Getter
    @AllArgsConstructor
    public enum ProductAttrType {

        /**
         * 基本信息
         */
        BASE_TYPE_ATTR(1,"基本信息"),
        /**
         * 销售属性
         */
        SALE_TYPE_ATTR(0, "销售属性");



        private int code;
        private String msg;

    }
    @Getter
    @AllArgsConstructor
    public enum Status {

        /**
         * 基本信息
         */
        SPU_CREATED(0,"新建"),
        /**
         * 销售属性
         */
        SPU_UP(1, "上架"),
        SPU_DOWN(2, "下架");



        private int code;
        private String msg;

    }

}
