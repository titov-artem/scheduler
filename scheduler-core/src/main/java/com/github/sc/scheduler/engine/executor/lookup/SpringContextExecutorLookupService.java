package com.github.sc.scheduler.engine.executor.lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;

/**
 * Lookup task executor using it's name like spring bean name
 */
public class SpringContextExecutorLookupService implements ExecutorLookupService, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringContextExecutorLookupService.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Optional<Runnable> get(String name) {
        try {
            Object bean = applicationContext.getBean(name);
            if (!(bean instanceof Runnable)) {
                log.warn("Bean {} is not Runnable", name);
                return Optional.empty();
            }
            return Optional.of((Runnable) bean);
        } catch (NoSuchBeanDefinitionException ignore) {
            log.warn("No bean with name {} found", name);
        } catch (RuntimeException e) {
            log.error("Failed to get bean " + name + " from context due to exception", e);
        }
        return Optional.empty();
    }
}
