package cn.bdqn.gulimall.to.mq;

import lombok.Data;

@Data
public class WareLockSkuTo {

    private Long taskId;

    private WareLockDetail detail;

}
