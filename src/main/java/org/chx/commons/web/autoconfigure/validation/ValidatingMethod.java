package org.chx.commons.web.autoconfigure.validation;

import java.lang.annotation.*;

/**
 * method used to validate current object, note that the annotated method should't have parameters
 *
 * @author chenxi
 * @date 2019-11-15
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatingMethod {

}
