package com.thefa.audit.config;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer extends GenericContainer<RedisContainer> {

    private RedisContainer() {
        super("redis:3.2");
    }

    public static RedisContainer build() {
        return new RedisContainer()
                .withExposedPorts(6379);
    }
}
