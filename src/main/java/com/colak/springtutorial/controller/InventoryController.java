package com.colak.springtutorial.controller;

import com.colak.springtutorial.model.StockInventory;
import com.colak.springtutorial.service.producer.InventoryProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryProducer inventoryProducer;

    // http://localhost:8080/inventory
    @GetMapping
    public Mono<Void> createInventory() throws JsonProcessingException {
        StockInventory stockInventory = new StockInventory();
        stockInventory.setId("1");
        stockInventory.setQty(2);
        stockInventory.setNumber("3");

        return inventoryProducer.createInventory(stockInventory);
    }
}
