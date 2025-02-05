package com.example.springdemo.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class GetOrderData {
    ArrayList<Order> asks;
    ArrayList<Order> bids;
    Orderbook orderbook;
    public GetOrderData(Orderbook orderbook){
        asks = new ArrayList<>();
        bids = new ArrayList<>();
        this.orderbook = orderbook;
    }
    public ArrayList<Order> getAsks() {
        return asks;
    }
    public ArrayList<Order> getBids() {
        return bids;
    }
    public BigDecimal getAskTotalVolume(){
        return orderbook.AskTotalVolume();
    }
    public BigDecimal getBidTotalVolume(){
        return orderbook.BidTotalVolume();
    }
}
