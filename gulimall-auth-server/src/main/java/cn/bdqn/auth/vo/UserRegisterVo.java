package cn.bdqn.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 加入 jsr30 校验
 */
@Data
public class UserRegisterVo {

    @NotNull(message = "用户名必须提交")
    @Length(min = 4, max = 20, message = "用户名长度 4 - 20")
    private String userName;

    @NotNull(message = "密码必须提交")
    @Length(min = 6, max = 20, message = "密码长度必须 6 - 20")
    private String password;

    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式错误")
    private String phone;

    @NotEmpty(message = "验证码必须提交")
    private String code;

}
