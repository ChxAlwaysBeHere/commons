package org.chx.commons.web.validation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * validate that target is valid
 *
 * @author chenxi
 * @date 2019-11-11
 */
public class ValidTargetValidator implements ConstraintValidator<ValidTarget, Object> {

    private static Logger log = LoggerFactory.getLogger(ValidTargetValidator.class);

    private static Map<Class<?>, List<Method>> validateMethodCache = Maps.newHashMap();

    @Override
    public void initialize(ValidTarget constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> targetClass = value.getClass();
        List<Method> validateMethods = validateMethodCache.get(targetClass);

        if (Objects.isNull(validateMethods)) {
            validateMethods = findValidatingMethod(targetClass);
            validateMethodCache.put(targetClass, validateMethods);
        }

        for (Method validateMethod : validateMethods) {
            ReflectionUtils.makeAccessible(validateMethod);
            Object validatingResult = ReflectionUtils.invokeMethod(validateMethod, value);
            if (validatingResult instanceof Boolean) {
                if (Objects.equals(validatingResult, Boolean.FALSE)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(getValidatingDefaultMessage(targetClass)).addConstraintViolation();
                    return false;
                }
            } else if (validatingResult instanceof ValidatingResult) {
                ValidatingResult result = (ValidatingResult) validatingResult;
                if (!result.isValid()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(result.getMessage()).addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }

    private List<Method> findValidatingMethod(Class<?> targetClass) {
        ValidTarget annotation = targetClass.getAnnotation(ValidTarget.class);

        List<Method> validateMethods = Lists.newArrayList();
        for (String methodName : annotation.methods()) {
            Method validateMethod = ReflectionUtils.findMethod(targetClass, methodName);
            if (Objects.isNull(validateMethod)) {
                log.error("method[{}] not found in  class[{}]", methodName, targetClass.getName());
            }
            if (validateMethod.getParameterCount() != 0) {
                log.error("method[{}] should not have parameters", methodName);
            }
            validateMethods.add(validateMethod);
        }
        return validateMethods;
    }

    private String getValidatingDefaultMessage(Class<?> targetClass) {
        return targetClass.getAnnotation(ValidTarget.class).message();
    }
}
