package com.colak.springtutorial.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQInit {

    public static final String EXCHANGE = "inventory.create";
    public static final String QUEUE = "queue.inventory.create";
    public static final String ROUTING_KEY = "inventory";

    private final AmqpAdmin amqpAdmin;

    // Method create exchanges, bindings, queues on start.
    @PostConstruct
    public void init() {
        // create exchanges
        amqpAdmin.declareExchange(ExchangeBuilder.directExchange(EXCHANGE).build());

        // create dlq-queues
        amqpAdmin.declareQueue(new Queue(QUEUE, false, false, false));

        // bind queues to exchanges
        amqpAdmin.declareBinding(BindingBuilder
                .bind(new Queue(QUEUE))
                .to(new DirectExchange(EXCHANGE))
                .with(ROUTING_KEY)
        );
    }


}
