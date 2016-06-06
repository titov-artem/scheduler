package com.github.sc.scheduler.core.model;

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
