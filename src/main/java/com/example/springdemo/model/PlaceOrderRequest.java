package com.example.springdemo.model;
import java.math.BigDecimal;

public class PlaceOrderRequest{
    String userId;
    OrderType orderType;
    boolean bid;
    BigDecimal size;
    BigDecimal price;
    Market market;
    public PlaceOrderRequest(String userId, OrderType orderType, boolean bid, BigDecimal size, BigDecimal price, Market market) {
        this.userId = userId.toLowerCase();
        this.orderType = orderType;
        this.bid = bid;
        this.size = size;
        this.price = price;
        this.market = market;
    }
    public Market getMarket() {
        return market;
    }
    public String getUserId() {
        return userId;
    }
    public boolean getBid(){
        return bid;
    }
    public BigDecimal getSize(){
        return size;
    }
    public BigDecimal getPrice(){
        return price;
    }
    public OrderType getOrderType(){
        return orderType;
    }
    public String toString(){
        return String.format("%.2f",size);
    }
}
