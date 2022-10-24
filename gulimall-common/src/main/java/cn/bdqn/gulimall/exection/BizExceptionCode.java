package cn.bdqn.gulimall.exection;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/22
 */
public enum BizExceptionCode {


    VALID_EXCEPTION(10000, "参数格式校验错误"),
    UNKNOW_EXCEPTION(10001, "未知错误异常"),
    NO_MANY_REQUEST(10003, "请求流量过大，请稍后再试！"),
    AUTH_CODE_EXCEPTION(1002,"发送信息验证码过于频繁，请稍后再试！"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USERNAME_EXISTS_EXCEPTION(15001, "用户名已存在"),
    PHONE_EXISTS_EXCEPTION(15002, "手机号已存在"),
    NO_HAS_STOCK_WARE(50000, "没有库存了"),
    LOGIN_MEMBER_INVALID_EXCEPTION(15003, "用户名或密码错误");


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
