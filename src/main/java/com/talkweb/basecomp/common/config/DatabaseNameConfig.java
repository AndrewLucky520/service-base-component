package com.talkweb.basecomp.common.config;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("database")
public class DatabaseNameConfig {

    private Map<String, String> databaseNames;

	public Map<String, String> getDatabaseNames() {
		return databaseNames;
	}

	public void setDatabaseNames(Map<String, String> databaseNames) {
		this.databaseNames = databaseNames;
	}

	public Properties getProperties() {
		if (databaseNames != null) {
			Properties p = new Properties();
			p.putAll(databaseNames);
			return p;
		}
		return null;
	}

}
