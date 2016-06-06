package com.github.sc.scheduler.utils;

import java.util.function.Supplier;

public interface TransactionSupport {

    <T> T doInTransaction(Supplier<T> supplier);

    void doInTransaction(Runnable action);

}
