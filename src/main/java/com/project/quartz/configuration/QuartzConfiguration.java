package com.project.quartz.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by user on 13:56 21/04/2025, 2025
 */

@Slf4j
@Configuration
public class QuartzConfiguration {

    private final DataSource dataSource;
    private final ApplicationContext applicationContext;
    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public QuartzConfiguration(DataSource dataSource, ApplicationContext applicationContext, PlatformTransactionManager platformTransactionManager) {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        AutowiringSpringBeanJobFactory autoWiringSpringBeanJobFactory = new AutowiringSpringBeanJobFactory();
        autoWiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(autoWiringSpringBeanJobFactory);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(platformTransactionManager);
        return schedulerFactoryBean;
    }

}
