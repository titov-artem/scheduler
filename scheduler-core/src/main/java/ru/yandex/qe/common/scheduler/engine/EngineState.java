package ru.yandex.qe.common.scheduler.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import ru.yandex.qe.common.scheduler.model.Run;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class EngineState {
    private static final Logger log = LoggerFactory.getLogger(EngineState.class);

    private final int maxThreadsCount;
    private final int maxCapacity;
    private final InternalCapacity capacity;
    /**
     * Store list of running tasks
     */
    final List<RunFuture> runningTasks;

    EngineState(int totalThreads, int totalCapacity) {
        this.capacity = new InternalCapacity(totalThreads, totalCapacity);
        this.maxCapacity = totalCapacity;
        this.maxThreadsCount = totalThreads;
        this.runningTasks = new CopyOnWriteArrayList<>();
    }

    boolean occupy(Run run) {
        return capacity.occupy(run.getEngineRequirements().getWeight());
    }

    void free(Run run) {
        if (!capacity.free(run.getEngineRequirements().getWeight())) {
            log.error(MarkerFactory.getMarker("FATAL"), "Engine capacity was increased by run with id {}!!!", run.getRunId());
        }
    }

    Capacity getCapacity() {
        return capacity.toCapacity();
    }

    @Override
    public String toString() {
        Capacity capacity = this.capacity.toCapacity();
        return String.format("State{%s threads, %s space}", capacity.freeThreads, capacity.freeCapacity);
    }

    private final class InternalCapacity {
        private int freeThreads;
        private int freeCapacity;
        private final ReadWriteLock lock;

        InternalCapacity(int freeThreads, int freeCapacity) {
            this.freeThreads = freeThreads;
            this.freeCapacity = freeCapacity;
            this.lock = new ReentrantReadWriteLock();
        }

        boolean occupy(int capacity) {
            return update(-1, -capacity);
        }

        boolean free(int capacity) {
            return update(1, capacity);
        }

        Capacity toCapacity() {
            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                return new Capacity(freeThreads, freeCapacity);
            } finally {
                readLock.unlock();
            }
        }

        private boolean update(int threadDelta, int capacityDelta) {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                int newThreads = freeThreads + threadDelta;
                int newCapacity = freeCapacity + capacityDelta;
                if (newThreads < 0 || newThreads > EngineState.this.maxThreadsCount) {
                    return false;
                }
                if (newCapacity < 0 || newCapacity > EngineState.this.maxCapacity) {
                    return false;
                }
                freeThreads = newThreads;
                freeCapacity = newCapacity;
                return true;
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public String toString() {
            return String.format("{freeThreads: %d, freeCapacity: %d}", freeThreads, freeCapacity);
        }
    }

    @Immutable
    static final class Capacity {
        final int freeThreads;
        final int freeCapacity;

        Capacity(int freeThreads, int freeCapacity) {
            this.freeThreads = freeThreads;
            this.freeCapacity = freeCapacity;
        }

        @Override
        public String toString() {
            return String.format("State{%s threads, %s space}", freeThreads, freeCapacity);
        }
    }
}
