package com.github.sc.scheduler.jdbc.utils;

import com.github.sc.scheduler.core.utils.TransactionSupport;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import java.util.function.Supplier;

/**
 * Transaction support for jdbc repositories. Use injected transaction operations
 * to maintain transaction
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class JdbcTransactionSupport implements TransactionSupport {

    private TransactionOperations transactionOperations;

    @Override
    public <T> T doInTransaction(Supplier<T> supplier) {
        return transactionOperations.execute(status -> supplier.get());
    }

    @Override
    public void doInTransaction(Runnable action) {
        transactionOperations.execute(status -> {
            action.run();
            return null;
        });
    }

    @Required
    public void setTransactionOperations(TransactionOperations transactionOperations) {
        this.transactionOperations = transactionOperations;
    }
}
