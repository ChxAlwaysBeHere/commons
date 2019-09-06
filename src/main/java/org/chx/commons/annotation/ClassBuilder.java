package org.chx.commons.annotation;

import java.lang.annotation.*;

/**
 * builder pattern
 *
 * @author chenxi
 * @date 2019-09-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClassBuilder {

    /**
     * builder class name
     *
     * @return
     */
    String value() default "";

}
