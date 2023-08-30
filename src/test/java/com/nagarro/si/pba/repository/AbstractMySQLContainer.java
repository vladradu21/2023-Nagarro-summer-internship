package com.nagarro.si.pba.repository;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractMySQLContainer {
    static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"));
        MY_SQL_CONTAINER.start();
    }
}