package ru.yandex.qe.common.scheduler.model;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface Task {

    @Nonnull
    String getId();

    @Nonnull
    Optional<String> getName();

    @Nonnull
    EngineRequirements getEngineRequirements();

}
