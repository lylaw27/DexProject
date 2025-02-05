package com.example.springdemo.model;

import java.util.ArrayList;

public class UserInfo {
    ArrayList<Order> bids;
    ArrayList<Order> asks;
    String userId;
    public UserInfo(){
        bids = new ArrayList<>();
        asks = new ArrayList<>();
    }

    public ArrayList<Order> getBids() {
        return bids;
    }

    public ArrayList<Order> getAsks() {
        return asks;
    }
}

