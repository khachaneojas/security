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
@ConditionalOnProperty(name = "spring.jpa.website.enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "websiteLocalContainerEntityManagerFactoryBean",
        transactionManagerRef = "websitePlatformTransactionManager",
        basePackages = {
            "com.sprk.commons.repository.website",
            "com.sprk.service.**.repository.website"
        }
)
public class WebsiteDataSourceConfiguration {

    @Bean(name = "websiteDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.jpa.website")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "websiteDataSource")
    public DataSource dataSource(@Qualifier("websiteDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "websiteLocalContainerEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("websiteDataSource") DataSource dataSource) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
//                "hibernate.dialect", "org.hibernate.dialect.MySQLDialect"
        );

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("com.sprk.commons.entity.website", "com.sprk.commons.repository.website", "com.sprk.service.**.repository.website");
        bean.setJpaPropertyMap(properties);
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return bean;
    }

    @Bean(name = "websitePlatformTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("websiteLocalContainerEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManager;
    }

}
