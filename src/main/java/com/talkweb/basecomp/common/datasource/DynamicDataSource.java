package com.talkweb.basecomp.common.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource{
    protected static final Log log = LogFactory.getLog(DynamicDataSource.class);

	private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<String>();

    public static void setDataSource(String dataSource) {
		log.debug("========setDataSource============" + dataSource);
        dataSourceKey.set(dataSource);
    }

    public static void clearDataSource() {
    	log.debug("=======clearDataSource=============");
		dataSourceKey.remove();
    }
	
	@Override
	protected Object determineCurrentLookupKey() {
		Object key = dataSourceKey.get();
		return key;
	}
	
	public static final String READ_DATASOURCE = "readDataSource";
	public static final String WRITE_DATASOURCE = "writeDataSource";
}
