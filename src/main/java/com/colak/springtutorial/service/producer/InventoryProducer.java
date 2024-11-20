package com.colak.springtutorial.service.producer;

import com.colak.springtutorial.config.RabbitMQInit;
import com.colak.springtutorial.model.StockInventory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryProducer {

    private final Sender sender;

    private final ObjectMapper mapper;

    // Method send message to queue with publish confirms
    public Mono<Void> createInventory(StockInventory stockInventory) throws JsonProcessingException {
        String json = mapper.writeValueAsString(stockInventory);
        byte[] inventoryByteArray = SerializationUtils.serialize(json);
        Flux<OutboundMessage> outboundFlux = Flux.just(new OutboundMessage("", RabbitMQInit.QUEUE, inventoryByteArray));


        log.info("Publish message: {}", stockInventory.toString());
        return sender.sendWithPublishConfirms(outboundFlux)
                .subscribeOn(Schedulers.boundedElastic())
                .filter(outboundMessageResult -> !outboundMessageResult.isAck())
                .handle((result, sink) -> sink.error(new Exception("Publish was not acked")))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)))
                .then();
    }

}
