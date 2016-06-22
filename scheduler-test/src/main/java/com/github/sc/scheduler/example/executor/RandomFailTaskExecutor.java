package com.github.sc.scheduler.example.executor;

import java.util.Random;

/**
 * Fail with probability 0.5
 */
public class RandomFailTaskExecutor extends SleepTaskExecutor {

    @Override
    public void run() {
        if (new Random().nextInt(100) < 50) throw new RuntimeException();
        super.run();
    }
}
