package org.chx.commons.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * the annotated target must be valid, validated by methods specified by {@linkplain ValidTarget#methods()}
 *
 * @author chenxi
 * @date 2019-11-11
 */
@Constraint(validatedBy = ValidTargetValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidTarget {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * specify which methods would be used for validation.<br/>
     * method should have no parameters and return type should be {@linkplain Void} or {@linkplain Boolean} or a {@link ValidatingResult}
     *
     * @return
     */
    String[] methods() default {"validate"};

}
