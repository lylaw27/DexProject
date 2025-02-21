package com.example.springdemo.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class UserInfo {
    ArrayList<Order> orders;
    String userId;
    BigDecimal shares;
    BigDecimal fmv;
    public UserInfo(){
        orders = new ArrayList<>();
        shares = new BigDecimal(0);
        fmv = new BigDecimal(0);
    }
    public BigDecimal getFmv() {
        return fmv;
    }
    public BigDecimal getShares() {
        return shares;
    }
    public void setShares(BigDecimal shares) {
        this.shares = shares;
        this.fmv = shares.multiply(new BigDecimal("9.1"));
    }
    public ArrayList<Order> getOrders() {
        return orders;
    }

}

