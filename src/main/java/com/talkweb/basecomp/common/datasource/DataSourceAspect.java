package com.talkweb.basecomp.common.datasource;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.talkweb.basecomp.Constants;

//@Aspect
//@Order(1)
//@Component
public class DataSourceAspect {

    @Pointcut("execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.select*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.get*(..))" +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.find*(..))" +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.query*(..))")
    public void readPointcut() {

    }

    @Pointcut("execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.insert*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.add*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.create*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.update*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.edit*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.delete*(..)) " +
            "|| execution(* " + Constants.BASE_PACKAGE_NAME + ".*.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
    	DynamicDataSource.setDataSource(DynamicDataSource.READ_DATASOURCE);
    }

    @Before("writePointcut()")
    public void write() {
    	DynamicDataSource.setDataSource(DynamicDataSource.WRITE_DATASOURCE);
    }
}
