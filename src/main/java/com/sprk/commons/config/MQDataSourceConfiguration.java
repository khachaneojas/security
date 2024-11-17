package com.sprk.commons.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;



@Configuration
@ConditionalOnProperty(name = "spring.jpa.mq.enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mqLocalContainerEntityManagerFactoryBean",
        transactionManagerRef = "mqPlatformTransactionManager",
        basePackages = {
            "com.sprk.commons.repository.mq",
            "com.sprk.service.**.repository.mq"
        }
)
public class MQDataSourceConfiguration {

    @Bean(name = "mqDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.jpa.mq")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mqDataSource")
    public DataSource dataSource(@Qualifier("mqDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "mqLocalContainerEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("mqDataSource") DataSource dataSource) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
//                "hibernate.dialect", "org.hibernate.dialect.MySQLDialect"
        );

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("com.sprk.commons.entity.mq", "com.sprk.commons.repository.mq", "com.sprk.service.**.repository.mq");
        bean.setJpaPropertyMap(properties);
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return bean;
    }

    @Bean(name = "mqPlatformTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("mqLocalContainerEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManager;
    }

}
