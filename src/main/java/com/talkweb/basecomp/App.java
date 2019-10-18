package com.talkweb.basecomp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@EnableEurekaClient
@EnableFeignClients(basePackages={Constants.BASE_PACKAGE_NAME})
@SpringBootApplication
@ComponentScan(basePackages={Constants.BASE_PACKAGE_NAME})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
public class App {
static final Logger logger = LoggerFactory.getLogger(App.class);
	
	private final Environment env;

    public App(Environment env) {
        this.env = env;
    }

	
    @PostConstruct
    public void init() {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(DEVELOPMENT) && activeProfiles.contains(PRODUCTION)) {
            logger.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(DEVELOPMENT) && activeProfiles.contains(TEST)) {
            logger.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'test' profiles at the same time.");
        }
    }
	
	public static void main(String[] args) throws UnknownHostException{
		SpringApplication app = new SpringApplication(App.class);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        logger.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getActiveProfiles());
		
	}

	public static final String DEVELOPMENT = "dev";
	public static final String PRODUCTION = "prod";
	public static final String TEST = "test";
	
}
