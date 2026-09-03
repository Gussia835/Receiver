package org.example.config;


import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.repository.pom",
        entityManagerFactoryRef = "pomEntityManagerFactory",
        transactionManagerRef = "pomTransactionManager"
)
public class PomDataSourceConfig {

    @Primary
    @Bean(name = "pomDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pom")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "pomEntityContainer")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("pomDataSource") DataSource dataSource,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("org.example.models.pom")
                .persistenceUnit("pom")
                .build();
    }

    @Primary
    @Bean(name = "pomTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("pomEntityContainer") LocalContainerEntityManagerFactoryBean container) {
        return new JpaTransactionManager(container.getObject());
    }


}
