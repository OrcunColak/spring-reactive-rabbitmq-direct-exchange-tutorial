package com.colak.springtutorial.repository;

import com.colak.springtutorial.model.StockInventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryRepository {

    private final ObjectMapper mapper;

    public Mono<Boolean> saveInventory(Delivery message) {

        String json = (String) SerializationUtils.deserialize(message.getBody());
        var inventory = readValue(json);
        if(Objects.isNull(inventory)) return Mono.just(Boolean.FALSE);

        log.info("######################################## Consumer DATA ####################################");
        log.info(">> Inventory data: {}", inventory);

        return Mono.just(Boolean.TRUE);
    }

    private StockInventory readValue(String body) {
        try {
            return mapper.readValue(body, StockInventory.class);
        } catch (IOException e) {
            log.error(">> Deserialize error");
            return null;
        }
    }

}
