package com.example.springdemo.model;
import java.math.BigDecimal;
import java.util.UUID;

public class Order {
    User user;
    UUID ID;
    BigDecimal size;
    BigDecimal orderSize;
    BigDecimal price;
    Limit limit;
    long timestamp;
    boolean bid;

    public Order(UUID orderId, User user, boolean bid, BigDecimal size) {
        this.user = user;
        this.size = size;
        this.orderSize = size;
        this.bid = bid;
        this.ID = orderId;
        timestamp = System.nanoTime();
    }

    public String getUserId() {
        return user.userId;
    }
    public UUID getOrderId() {
        return ID;
    }
    public BigDecimal getOrderSize() {return orderSize;}
    public BigDecimal getSize() {
        return size;
    }
    public BigDecimal getPrice() {return price;}
    public long getTimestamp() {
        return timestamp;
    }
    public boolean getBid() {
        return bid;
    }
    public boolean isFilled() {
        return this.size.compareTo(BigDecimal.ZERO) == 0;
    }

}
