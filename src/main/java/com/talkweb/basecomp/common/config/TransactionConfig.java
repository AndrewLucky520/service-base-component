package com.talkweb.basecomp.common.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.talkweb.basecomp.common.datasource.DynamicTransactionManager;

@Configuration
public class TransactionConfig {

    @Resource(name = "dynamicDataSource")
    private DataSource dynamicDataSource;

    @Bean
    public PlatformTransactionManager txManager() {
        return new DynamicTransactionManager(dynamicDataSource);
    }
    
    @Bean(name = "txAdvice")
    public TransactionInterceptor txAdvice() {
    	RuleBasedTransactionAttribute txAttr_REQUIRED = new RuleBasedTransactionAttribute();
        txAttr_REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txAttr_REQUIRED.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        
        RuleBasedTransactionAttribute txAttr_REQUIRED_READONLY = new RuleBasedTransactionAttribute();
        txAttr_REQUIRED_READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        txAttr_REQUIRED_READONLY.setReadOnly(true);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("create*", txAttr_REQUIRED);
        source.addTransactionalMethod("add*", txAttr_REQUIRED);
        source.addTransactionalMethod("edit*", txAttr_REQUIRED);
        source.addTransactionalMethod("insert*", txAttr_REQUIRED);
        source.addTransactionalMethod("delete*", txAttr_REQUIRED);
        source.addTransactionalMethod("update*", txAttr_REQUIRED);
        source.addTransactionalMethod("get*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("find*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("query*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("select*", txAttr_REQUIRED_READONLY);
        return new TransactionInterceptor(txManager(), source);
 
    }
 
    @Bean
    public BeanNameAutoProxyCreator txProxy() {
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setInterceptorNames("txAdvice");
        creator.setBeanNames("*Service", "*ServiceImpl");
        creator.setProxyTargetClass(true);
        return creator;
    }
}