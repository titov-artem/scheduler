package com.github.sc.scheduler.core.engine.executor.lookup;

import com.github.sc.scheduler.core.engine.RunnableTaskExecutor;
import com.github.sc.scheduler.core.engine.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;

/**
 * Lookup task executor using it's name like spring bean name
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class SpringContextExecutorLookupService implements ExecutorLookupService, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringContextExecutorLookupService.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Optional<TaskExecutor> get(String name) {
        try {
            Object bean = applicationContext.getBean(name);
            if (bean instanceof TaskExecutor) {
                return Optional.of((TaskExecutor) bean);
            }
            if (bean instanceof Runnable) {
                return Optional.of(new RunnableTaskExecutor((Runnable) bean));
            }

            log.warn("Bean {} is not Runnable or TaskExecutor", name);
        } catch (NoSuchBeanDefinitionException ignore) {
            log.warn("No bean with name {} found", name);
        } catch (RuntimeException e) {
            log.error("Failed to get bean " + name + " from context due to exception", e);
        }
        return Optional.empty();
    }
}
