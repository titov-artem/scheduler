package com.github.sc.scheduler.core.engine;

import com.github.sc.scheduler.core.model.EngineDescriptor;
import com.github.sc.scheduler.core.model.EngineRequirements;
import com.github.sc.scheduler.core.model.Run;

import java.util.ArrayList;
import java.util.List;

/**
 * Pick tasks in their order.
 *
 * @author Artem Titov titov.artem.u@yandex.com
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
