package com.example.springdemo.model;

import java.math.BigDecimal;

public class Trade {
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
