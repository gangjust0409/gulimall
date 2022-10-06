package cn.bdqn.gulimall.product.anon;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/22
 * validatedBy 可以有多个
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = { StatusConstraintValidator.class }
)
public @interface Status {

    String message() default "{cn.bdqn.gulimall.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};

}
