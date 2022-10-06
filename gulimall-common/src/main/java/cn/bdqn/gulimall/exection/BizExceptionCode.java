package cn.bdqn.gulimall.exection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/22
 */
public enum BizExceptionCode {


    VALID_EXCEPTION(10000, "参数格式校验错误"),
    UNKNOW_EXCEPTION(10001, "未知错误异常"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常");


    BizExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
