package com.colak.springtutorial.config;

import com.rabbitmq.client.Connection;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConnectionCloseConfig {

    private final Mono<Connection> connectionMono;

    @PreDestroy
    public void close() throws IOException {
        Objects.requireNonNull(connectionMono.block()).close();
    }

}
