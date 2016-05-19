package ru.yandex.qe.common.scheduler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Local task scheduler with task's exceptions handling
 *
 * @author scorpion@yandex-team on 12.03.15.
 */
public final class LocalTaskScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskScheduler.class);

    private final ScheduledExecutorService scheduledExecutorService;

    public LocalTaskScheduler(final int threadCount) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(threadCount);
    }

    public ScheduledFuture<?> scheduleOnce(final String taskName,
                                           final Runnable sourceTask,
                                           final long delay,
                                           final TimeUnit unit) {
        final AtomicReference<ScheduledFuture<?>> out = new AtomicReference<>();
        schedule(taskName, sourceTask, true, new ScheduleMethod() {
            @Override
            public ScheduledFuture<?> schedule(final Runnable task, final ScheduledExecutorService executorService) {
                final ScheduledFuture<?> future = executorService.schedule(task, delay, unit);
                out.set(future);
                return future;
            }

            @Nullable
            @Override
            public ScheduledFuture<?> reschedule(final Runnable taskToRun, final ScheduledExecutorService executorService) {
                return null;
            }
        });
        return out.get();
    }

    public void scheduleWithFixRate(final String taskName,
                                    final Runnable sourceTask,
                                    final long initialDelay,
                                    final long period,
                                    final TimeUnit unit) {
        schedule(taskName, sourceTask, false, new ScheduleMethod() {
            @Override
            public ScheduledFuture<?> schedule(final Runnable task, final ScheduledExecutorService executorService) {
                return executorService.scheduleAtFixedRate(task, initialDelay, period, unit);
            }

            @Override
            public ScheduledFuture<?> reschedule(final Runnable task, final ScheduledExecutorService executorService) {
                return executorService.scheduleAtFixedRate(task, period, period, unit);
            }
        });
    }

    public boolean isShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    public boolean isTerminated() {
        return scheduledExecutorService.isTerminated();
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    public void shutdownNow() {
        scheduledExecutorService.shutdownNow();
    }

    private <T> void schedule(final String taskName, final Runnable task, final boolean onceAction, final ScheduleMethod scheduleMethod) {
        final String threadName = taskName + "Thread";
        final Runnable taskToRun = () -> {
            final Thread thread = Thread.currentThread();
            final String name = thread.getName();
            thread.setName(threadName);
            try {
                task.run();
            } finally {
                thread.setName(name);
            }
        };

        final Thread watchDog = new Thread(() -> {
            ScheduledFuture<?> future = scheduleMethod.schedule(taskToRun, scheduledExecutorService);
            while (true) {
                try {
                    future.get();
                    if (onceAction) {
                        LOG.info("Task {} must scheduled once. Stopping watchdog", taskName);
                        return;
                    }
                } catch (InterruptedException e) {
                    LOG.error(MarkerFactory.getMarker("FATAL"), Thread.currentThread().getName() + " was interrupted", e);
                    return;
                } catch (ExecutionException e) {
                    LOG.error(MarkerFactory.getMarker("FATAL"), "Task " + taskName + " failed due to throwable", e.getCause());
                    future = scheduleMethod.reschedule(taskToRun, scheduledExecutorService);
                    if (future == null) {
                        LOG.info("Task {} not support rescheduling. Stopping watchdog", taskName);
                        return;
                    }
                } catch (CancellationException e) {
                    LOG.info("Task " + taskName + " was canceled. Stopping watchdog.", e);
                    return;
                }
            }
        });
        watchDog.setDaemon(true);
        watchDog.setName(taskName + "Watchdog");
        watchDog.start();
    }

    private interface ScheduleMethod {
        /**
         * Schedule task for first time
         *
         * @param task
         * @return
         */
        ScheduledFuture<?> schedule(Runnable task, ScheduledExecutorService executorService);

        /**
         * Schedule task if previous execution fails
         *
         * @param taskToRun
         * @return null if rescheduling not supported
         */
        @Nullable
        ScheduledFuture<?> reschedule(Runnable taskToRun, ScheduledExecutorService executorService);
    }

}
