package com.talkweb.basecomp.common.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


/**
 * <p>Description:MyBatis 配置</p>
 * <p>创建日期:2017年12月14日</p>
 * @author thh
 * @version 1.0
 * <p>拓维教育营销体系-技术平台部</p>
 */
@Configuration
public class MyBatisConfig {
    //private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
    
    @Resource(name = "dynamicDataSource")
    private DataSource dynamicDataSource;

    @Resource(name = "databaseNameConfig")
    private DatabaseNameConfig databaseNameConfig;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:mapper/mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*/*.xml"));
        sqlSessionFactoryBean.setConfigurationProperties(databaseNameConfig.getProperties());
        return sqlSessionFactoryBean.getObject();
    }

}
