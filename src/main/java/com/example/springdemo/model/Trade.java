package com.example.springdemo.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Trade {
    UUID ID;
    BigDecimal price;
    BigDecimal size;
    boolean bid;
    long timestamp;

    public Trade(BigDecimal price, BigDecimal size, boolean bid) {
        this.price = price;
        this.size = size;
        this.bid = bid;
        timestamp = System.nanoTime();
    }

    public UUID getID() {
        return ID;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSize() {
        return size;
    }

    public boolean isBid() {
        return bid;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
