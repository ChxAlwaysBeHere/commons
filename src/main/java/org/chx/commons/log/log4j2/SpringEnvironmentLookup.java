package org.chx.commons.log.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * log4j2 lookup extension, supports spring environment properties
 *
 * @author chenxi
 * @date 2019-09-04
 */
@Plugin(name = "spring", category = StrLookup.CATEGORY)
public class SpringEnvironmentLookup extends AbstractLookup implements GenericApplicationListener {

    private final static Class<? extends ApplicationEvent> SUPPORT_EVENT = ApplicationEnvironmentPreparedEvent.class;

    private static Environment environment;

    @Override
    public String lookup(LogEvent logEvent, String s) {
        return Objects.nonNull(environment) ? environment.getProperty(s) : null;
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return SUPPORT_EVENT.isAssignableFrom(eventType.getRawClass());
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent environmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) event;
            environment = environmentPreparedEvent.getEnvironment();
        }
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 1;
    }
}
