package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.repository.gru",
        entityManagerFactoryRef = "gruEntityManagerFactory",
        transactionManagerRef = "gruTransactionManager"
)

public class GruDataSourceConfig {

    @Value("${receiver.datasource.gru.url}")
    private String url;

    @Value("${receiver.datasource.gru.username}")
    private String user;

    @Value("${receiver.datasource.gru.password}")
    private String password;


    @Value("${receiver.datasource.gru.driver-class-name}")
    private String driverName;

    @Bean(name = "gruDataSource")
  //  @ConfigurationProperties(prefix = "receiver.datasource.gru")

    public DataSource dataSource() {

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverName);
        dataSourceBuilder.username(user);
        dataSourceBuilder.password(password);
        dataSourceBuilder.url(url);


        return dataSourceBuilder.build();
    }

    @Bean(name = "gruEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gruDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("org.example.models.gru")
                .persistenceUnit("gru")
                .build();
    }

    @Bean(name = "gruTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("gruEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}