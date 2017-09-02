package springbook.learningtest.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReadOnlyRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly =
                TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        System.out.println("DataSource: " + (readOnly ? "READONLY" : "READWRITE"));
        return readOnly ? "READONLY" : "READWRITE";
    }
}
