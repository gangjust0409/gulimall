package cn.bdqn.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {

    private String catalog1Id;
    private List<CateLog3Vo> catalog3List;
    private String id;
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CateLog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }

}
