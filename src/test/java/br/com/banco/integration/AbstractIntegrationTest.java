package br.com.banco.integration;

import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Tag("integration")
public abstract class AbstractIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("banco_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema-test.sql");

    protected static String jdbcUrl() {
        return POSTGRES.getJdbcUrl();
    }

    protected static String username() {
        return POSTGRES.getUsername();
    }

    protected static String password() {
        return POSTGRES.getPassword();
    }
}