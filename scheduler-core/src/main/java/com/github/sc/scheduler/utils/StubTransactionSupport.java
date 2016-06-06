package com.github.sc.scheduler.utils;

import java.util.function.Supplier;

/**
 * Stub transaction support don't create any transactions
 */
public class StubTransactionSupport implements TransactionSupport {

    /**
     * Stub just call to {@link Supplier#get()}
     *
     * @param supplier
     * @param <T>
     * @return
     */
    @Override
    public <T> T doInTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    /**
     * Stub just call to {@link Runnable#run()}
     *
     * @param action
     */
    @Override
    public void doInTransaction(Runnable action) {
        action.run();
    }
}
