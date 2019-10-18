package com.talkweb.basecomp.common.datasource;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

public class DynamicTransactionManager extends DataSourceTransactionManager {
	private static final long serialVersionUID = 1L;

	public DynamicTransactionManager() {
		super();
	}

	public DynamicTransactionManager(DataSource dataSource) {
		super(dataSource);
	}

	@Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        boolean readOnly = definition.isReadOnly();
        if (readOnly)
        	DynamicDataSource.setDataSource("readDataSource");
        else
        	DynamicDataSource.setDataSource("writeDataSource");
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DynamicDataSource.clearDataSource();
    }
}
