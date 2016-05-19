package ru.yandex.qe.common.scheduler.repo;

import ru.yandex.qe.common.scheduler.model.TaskParams;

import java.util.Optional;

public interface TaskParamsRepository {

    Optional<TaskParams> get(String taskId);

    void save(String taskId, TaskParams taskParams);

}
