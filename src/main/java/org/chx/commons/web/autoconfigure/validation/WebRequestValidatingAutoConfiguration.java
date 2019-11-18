package org.chx.commons.web.autoconfigure.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * auto configuration that validate web request parameters
 *
 * @author chenxi
 * @date 2019-11-18
 */
@ConditionalOnClass(ValidatingMethod.class)
@Configuration
public class WebRequestValidatingAutoConfiguration {

    @ControllerAdvice
    public static class WebRequestValidationControllerAdvice {

        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.addValidators(new ValidatingMethodValidator());
        }

    }

    /**
     * spring validator
     */
    public static class ValidatingMethodValidator implements Validator {

        private static Logger logger = LoggerFactory.getLogger(ValidatingMethodValidator.class);

        private static Map<Class<?>, Set<Method>> validatingMethodMap = new HashMap<>();

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public void validate(Object target, Errors errors) {
            Class<?> targetClass = target.getClass();
            Set<Method> validatingMethods = validatingMethodMap.get(targetClass);

            if (Objects.isNull(validatingMethods)) {
                validatingMethods = MethodIntrospector.selectMethods(targetClass, new ReflectionUtils.MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        return Objects.nonNull(AnnotationUtils.findAnnotation(method, ValidatingMethod.class));
                    }
                });
                validatingMethodMap.put(targetClass, validatingMethods);
            }

            for (Method validatingMethod : validatingMethods) {
                try {
                    ReflectionUtils.makeAccessible(validatingMethod);
                    validatingMethod.invoke(target);
                } catch (IllegalAccessException e) {
                    logger.warn("illegal access: {}.{}", targetClass.getName(), validatingMethod.getName(), e);
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    } else {
                        throw new RuntimeException(e.getCause());
                    }
                }
            }
        }
    }

}
