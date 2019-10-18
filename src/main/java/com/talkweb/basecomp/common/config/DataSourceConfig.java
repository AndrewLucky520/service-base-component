package com.talkweb.basecomp.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.talkweb.basecomp.common.datasource.DynamicDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSource readDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        return new DruidDataSource();
    }

    @Bean
    public DataSource dynamicDataSource(@Qualifier("readDataSource") DataSource readDataSource,
                                    @Qualifier("writeDataSource") DataSource writeDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("readDataSource", readDataSource);
        targetDataSources.put("writeDataSource", writeDataSource);
        DynamicDataSource myRoutingDataSource = new DynamicDataSource();
        myRoutingDataSource.setDefaultTargetDataSource(readDataSource);
        myRoutingDataSource.setTargetDataSources(targetDataSources);
        return myRoutingDataSource;
    }
    
}
