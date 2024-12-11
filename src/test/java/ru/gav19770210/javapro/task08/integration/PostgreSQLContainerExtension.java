package ru.gav19770210.javapro.task08.integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLContainerExtension implements BeforeAllCallback, AfterAllCallback {
    private final String databaseName = "javapro_task8";
    private final String username = "postgres";
    private final String password = "rootroot";
    private PostgreSQLContainer<?> postgreSQLContainer;

    @Override
    public void beforeAll(ExtensionContext context) {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

        postgreSQLContainer.withDatabaseName(this.databaseName)
                .withUsername(this.username)
                .withPassword(this.password)
                .withExposedPorts(5432);

        postgreSQLContainer.start();

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        postgreSQLContainer.stop();
        postgreSQLContainer = null;
    }
}
