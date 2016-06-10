package com.github.sc.scheduler.core.utils;

import java.util.function.Supplier;

/**
 * Stub transaction support don't create any transactions
 *
 * @author Artem Titov titov.artem.u@yandex.com
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
