package com.example.springdemo.model;

import java.math.BigDecimal;

public class Candle {
    long timestamp;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    public Candle(BigDecimal price,long timestamp) {
        this.timestamp = timestamp;
        open = price;
        high = price;
        low = price;
        close = price;
    }
    public void update(BigDecimal price){
        if(price.compareTo(high) > 0){
            high = price;
        }
        if(price.compareTo(low) < 0){
            low = price;
        }
        close = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }
}
