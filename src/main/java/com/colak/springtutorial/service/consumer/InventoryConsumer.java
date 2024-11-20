package com.colak.springtutorial.service.consumer;

import com.colak.springtutorial.config.RabbitMQInit;
import com.colak.springtutorial.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.AcknowledgableDelivery;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.Receiver;

import java.time.Duration;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {

    private final InventoryRepository inventoryRepository;

    private final Receiver receiver;

    @PostConstruct
    public Disposable receiveMessage() {
        return receiver.consumeManualAck(RabbitMQInit.QUEUE, consumeOptions())
                .publishOn(Schedulers.parallel())
                .filter(delivery -> Objects.nonNull(delivery.getBody()))
                .flatMap(this::consumer)
                .subscribe();
    }

    private Mono<?> consumer(AcknowledgableDelivery acknowledgableDelivery) {
        return inventoryRepository.saveInventory(acknowledgableDelivery)
                .doOnSuccess(consume -> {
                    if (Boolean.TRUE.equals(consume)) {
                        acknowledgableDelivery.ack();
                    } else {
                        acknowledgableDelivery.nack(false);
                    }
                })
                .onErrorResume(throwable -> {
                    log.error(">> Exception Error => ", throwable);
                    return Mono.fromRunnable(() -> acknowledgableDelivery.nack(false));
                });
    }

    private ConsumeOptions consumeOptions() {
        return new ConsumeOptions().exceptionHandler(
                new ExceptionHandlers.RetryAcknowledgmentExceptionHandler(
                        Duration.ofSeconds(20),
                        Duration.ofMillis(500),
                        ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
                )
        );
    }
}
