package cn.bdqn.cart.to;

import lombok.Data;

@Data
public class UserInfoTo {

    private Long userId;
    private String userKey;
    private Boolean isTemp = false; // 用于判断浏览器带有cookie

}
