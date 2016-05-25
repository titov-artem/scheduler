package ru.yandex.qe.common.scheduler.engine;

import ru.yandex.qe.common.scheduler.model.EngineDescriptor;
import ru.yandex.qe.common.scheduler.model.EngineRequirements;
import ru.yandex.qe.common.scheduler.model.Run;

import java.util.ArrayList;
import java.util.List;

/**
 * Pick tasks in their order.
 */
public class SimpleInOrderTaskPicker implements TaskPicker {

    @Override
    public List<Run> pickRuns(List<Run> runs, EngineDescriptor descriptor) {
        List<Run> out = new ArrayList<>();
        for (final Run run : runs) {
            EngineRequirements requirements = run.getEngineRequirements();
            if (!descriptor.getService().equals(requirements.getService())) {
                continue;
            }
            if (requirements.getWeight() > descriptor.getFreeCapacity()) {
                break;
            }
            out.add(run);
        }
        return out;
    }
}
