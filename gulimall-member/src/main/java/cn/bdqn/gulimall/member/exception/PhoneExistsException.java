package cn.bdqn.gulimall.member.exception;

public class PhoneExistsException extends RuntimeException {
    public PhoneExistsException() {
        super("手机号已存在");
    }
}
