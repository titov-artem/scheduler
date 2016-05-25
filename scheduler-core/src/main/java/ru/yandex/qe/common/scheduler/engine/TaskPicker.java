package ru.yandex.qe.common.scheduler.engine;

import ru.yandex.qe.common.scheduler.model.EngineDescriptor;
import ru.yandex.qe.common.scheduler.model.Run;

import java.util.List;

// todo add better understanding of this interface
public interface TaskPicker {

    /**
     * Have to pick runs from specified ones.
     * Any picked run's engine requirements must much to specified descriptor.
     * Also each run's weight must be less or equal engine free capacity.
     * Runs must have an order in which engine have to start them.
     * Engine will start iterate over returned runs and will start any
     * if there are enough threads and capacity to start it.
     *
     * @param runs
     * @param descriptor
     * @return maybe empty list of reserved runs
     */
    List<Run> pickRuns(List<Run> runs, EngineDescriptor descriptor);

}
