package cn.bdqn.gulimall.product.anon;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/22
 */
public class StatusConstraintValidator implements ConstraintValidator<Status, Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化
     * @param constraintAnnotation
     */
    @Override
    public void initialize(Status constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int s: vals) {
            set.add(s);
        }
    }
    /**
     * 判断
     * @param val 需要校验的值
     * @param constraintValidatorContext 上下文环境
     * @return
     */
    @Override
    public boolean isValid(Integer val, ConstraintValidatorContext constraintValidatorContext) {

        return set.contains(val);
    }

}
