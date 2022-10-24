package cn.bdqn.auth.vo;

import lombok.Data;

@Data
public class SocialUser {
    private String accessToken;
    private String remindIn;
    private Long expiresIn;
    private String uid;
    private String isrealname;

}
