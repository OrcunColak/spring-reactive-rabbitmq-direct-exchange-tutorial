package com.colak.springtutorial.model;

import lombok.Data;

@Data
public class StockInventory {

    private String id;
    private String number;
    private double qty;
}
