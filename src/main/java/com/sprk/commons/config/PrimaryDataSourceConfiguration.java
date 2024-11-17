package com.sprk.commons.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;



@Configuration
@ConditionalOnProperty(name = "spring.jpa.primary.enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "primaryLocalContainerEntityManagerFactoryBean",
        transactionManagerRef = "primaryPlatformTransactionManager",
        basePackages = {
                "com.sprk.commons.repository.primary",
                "com.sprk.service.**.repository.primary"
        }
)
public class PrimaryDataSourceConfiguration {

    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.jpa.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource dataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean(name = "primaryLocalContainerEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("primaryDataSource") DataSource dataSource) {
        Map<String, String> properties = Map.of(
//                "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",
//                "hibernate.show_sql", "true",
//                "hibernate.format_sql", "true",
//                "hibernate.generate_statistics", "true",
                "hibernate.hbm2ddl.auto", "update"
        );

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("com.sprk.commons.entity.primary", "com.sprk.commons.repository.primary", "com.sprk.service.**.repository.primary");
        bean.setJpaPropertyMap(properties);
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return bean;
    }

    @Primary
    @Bean(name = "primaryPlatformTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("primaryLocalContainerEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManager;
    }

}
