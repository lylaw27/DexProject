package com.example.springdemo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class UserInfo {
    Deque<Order> orders;
    String userId;
    BigDecimal shares;
    BigDecimal purchasedAmount;
    public UserInfo(){
        orders = new LinkedList<>();
        shares = new BigDecimal(0);
        purchasedAmount = new BigDecimal(0);
    }
    public BigDecimal getPurchasedAmount() {
        return purchasedAmount;
    }
    public BigDecimal getShares() {
        return shares;
    }
    public void setShares(BigDecimal shares) {
        this.shares = shares;
        this.purchasedAmount = shares.multiply(new BigDecimal("40"));
    }
    public BigDecimal getTotalVolume() {
        return orders.stream().map(Order::getSize).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public Deque<Order> getOrders() {
        return orders;
    }
    public void addOrder(Order order){
        orders.addFirst(order);
        if(orders.size() > 50){
            orders.removeLast();
        }
    }
}

