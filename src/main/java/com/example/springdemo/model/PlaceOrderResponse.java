package com.example.springdemo.model;


import java.math.BigDecimal;
import java.util.UUID;

public class PlaceOrderResponse extends PlaceOrderRequest{
    UUID orderId;
    public PlaceOrderResponse(String userId, OrderType orderType, boolean bid, BigDecimal size, BigDecimal price, Market market) {
        super(userId, orderType, bid, size, price, market);
        this.orderId = UUID.randomUUID();
    }

    public UUID getOrderId() {
        return orderId;
    }

}
