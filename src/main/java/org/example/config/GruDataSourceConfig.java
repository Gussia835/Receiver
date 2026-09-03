package org.example.config;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "org.example.repository.gru",
        entityManagerFactoryRef = "gruEntityManagerFactory",
        transactionManagerRef = "gruTransactionManager"
)
public class GruDataSourceConfig {

    @Bean(name = "gruDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.gru")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gruEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("gruDataSource") DataSource dataSource,
                                                                       EntityManagerFactoryBuilder builder) {

        return builder
                .dataSource(dataSource)
                .packages("org.example.models.gru")
                .persistenceUnit("gru")
                .build();
    }

    @Bean(name = "gruTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("gruEntityManagerFactory") LocalContainerEntityManagerFactoryBean container) {
        return new JpaTransactionManager(container.getObject());
    }


}