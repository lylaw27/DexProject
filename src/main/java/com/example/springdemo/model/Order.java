package com.example.springdemo.model;
import java.math.BigDecimal;
import java.util.UUID;

public class Order {
    User user;
    UUID ID;
    BigDecimal size;
    Limit limit;
    long timestamp;
    boolean bid;

    public Order(UUID orderId, User user, boolean bid, BigDecimal size) {
        this.user = user;
        this.size = size;
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
    public BigDecimal getSize() {
        return size;
    }
    public BigDecimal getPrice() {
        return limit.price;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public boolean getBid() {
        return bid;
    }

    boolean isFilled() {
        return this.size.compareTo(BigDecimal.ZERO) == 0;
    }

    public String toString() {
        return String.format("size: %.02f", size);
    }
}
