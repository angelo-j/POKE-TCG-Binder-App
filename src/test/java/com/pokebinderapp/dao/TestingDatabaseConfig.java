package com.pokebinderapp.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokebinderapp.services.PokemonApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

@ComponentScan(basePackages = {"com.techelevator.services", "com.techelevator.config"})
@Configuration
public class TestingDatabaseConfig {
    // To use an existing PostgreSQL database, set the following environment variables.
    // Otherwise, a temporary database will be created on the local machine.
    private static final String DB_HOST =
            Objects.requireNonNullElse(System.getenv("DB_HOST"), "localhost");
    private static final String DB_PORT =
            Objects.requireNonNullElse(System.getenv("DB_PORT"), "5432");
    private static final String DB_NAME =
            Objects.requireNonNullElse(System.getenv("DB_NAME"), "m2_final_project_test");
    private static final String DB_USERNAME =
            Objects.requireNonNullElse(System.getenv("DB_USERNAME"), "postgres");
    private static final String DB_PASSWORD =
            Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "postgres1");


    private SingleConnectionDataSource adminDataSource;
    private JdbcTemplate adminJdbcTemplate;

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void setup() {
        if (System.getenv("DB_HOST") == null) {
            adminDataSource = new SingleConnectionDataSource();
            adminDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
            adminDataSource.setUsername("postgres");
            adminDataSource.setPassword("postgres1");
            adminJdbcTemplate = new JdbcTemplate(adminDataSource);
            adminJdbcTemplate.update("DROP DATABASE IF EXISTS \"" + DB_NAME + "\";");
            adminJdbcTemplate.update("CREATE DATABASE \"" + DB_NAME + "\";");
        }
    }

    private DataSource ds = null;

    @Bean
    public DataSource dataSource() throws SQLException {
        if(ds != null) return ds;

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME));
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setAutoCommit(false); //So we can rollback after each test.

        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));

        ds = dataSource;
        return ds;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public JdbcUserDao userDao(JdbcTemplate jdbcTemplate) {
        return new JdbcUserDao(jdbcTemplate);
    }

    @Bean
    public JdbcCardDao cardDao(JdbcTemplate jdbcTemplate, PokemonApiService pokemonApiService) {
        return new JdbcCardDao(jdbcTemplate, pokemonApiService);
    }

    @Bean
    public JdbcBinderDao binderDao(JdbcTemplate jdbcTemplate, JdbcCardDao cardDao) {
        JdbcBinderDao binderDao = new JdbcBinderDao(jdbcTemplate, cardDao);
        return binderDao;
    }

    @PreDestroy
    public void cleanup() throws SQLException {
        if (adminDataSource != null) {
            // Terminate all active connections to the test database
            adminJdbcTemplate.update(
                    "SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                            "FROM pg_stat_activity " +
                            "WHERE pg_stat_activity.datname = ? AND pid <> pg_backend_pid();", DB_NAME);

            // Now drop the database
            adminJdbcTemplate.update("DROP DATABASE \"" + DB_NAME + "\";");

            adminDataSource.getConnection().close();
            adminDataSource.destroy();
        }
    }

}
