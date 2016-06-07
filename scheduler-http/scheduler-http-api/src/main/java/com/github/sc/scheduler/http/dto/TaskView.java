package com.github.sc.scheduler.http.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.sc.scheduler.core.model.Run;
import com.github.sc.scheduler.core.model.SchedulingParams;
import com.github.sc.scheduler.core.model.TaskArgs;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Immutable
public class TaskView extends TaskForm {

    private final List<RunView> activeRun;

    public TaskView(SchedulingParams schedulingParams,
                    @Nullable TaskArgs taskArgs,
                    List<Run> activeRun) {
        super(schedulingParams, taskArgs);
        this.activeRun = activeRun.stream().map(RunView::new).collect(toList());
    }

    @JsonGetter
    public List<RunView> getActiveRun() {
        return activeRun;
    }
}
