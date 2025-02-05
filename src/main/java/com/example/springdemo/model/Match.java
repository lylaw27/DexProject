package com.example.springdemo.model;
import java.math.BigDecimal;
import java.util.UUID;

public class Match {
    UUID ID;
    Order ask;
    Order bid;
    BigDecimal sizeFilled;
    BigDecimal price;

    public Match(UUID orderId,Order ask, Order bid, BigDecimal size, BigDecimal price) {
        this.ID = orderId;
        this.ask = ask;
        this.bid = bid;
        this.sizeFilled = size;
        this.price = price;
    }

    public UUID getID() {
        return ID;
    }

    public BigDecimal getSizeFilled() {
        return sizeFilled;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String toString() {
        return String.format("ask: %.2f | bid: %.2f | sizeFilled: %.2f|  price: %.2f", ask.size, bid.size, sizeFilled, price);
    }
}
