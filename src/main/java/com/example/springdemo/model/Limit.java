package com.example.springdemo.model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Limit {
    BigDecimal price;
    SortedSet<Order> orderList;
    BigDecimal totalVolume;

    public Limit(BigDecimal price) {
        this.price = price;
        totalVolume = new BigDecimal(0);
        this.orderList = new TreeSet<>(Comparator.comparingLong(a -> a.timestamp));
    }

    public void AddOrder(Order order) {
        order.limit = this;
        orderList.add(order);
        totalVolume = totalVolume.add(order.size);
    }

    public void DeleteOrder(Order order) {
        order.limit = null;
        orderList.remove(order);
        if(order.bid){
            order.user.bids.remove(order);
        }
        else{
            order.user.asks.remove(order);
        }
        totalVolume = totalVolume.subtract(order.size);
    }

    public ArrayList<Match> Fill(Order incomingOrder) {
        ArrayList<Match> matches = new ArrayList<>();
        ArrayList<Order> emptyOrders = new ArrayList<>();
        for (Order bookOrder : this.orderList) { //loop through the limits
            Match match = this.FillOrder(incomingOrder, bookOrder);
            matches.add(match);
            totalVolume = totalVolume.subtract(match.sizeFilled);
            if (bookOrder.isFilled()) {
                emptyOrders.add(bookOrder);
            }
            if (incomingOrder.isFilled()) {
                break;
            }
        }
        for (Order bookOrder : emptyOrders) {
            this.DeleteOrder(bookOrder);
        }
        return matches;
    }

    public Match FillOrder(Order orderA, Order orderB) {
        Order ask;
        Order bid;
        BigDecimal sizeFilled;
        if (orderA.size.compareTo(orderB.size)>0) {
            orderA.size = orderA.size.subtract(orderB.size);
            sizeFilled = orderB.size;
            orderB.size = new BigDecimal(0);
        } else {
            orderB.size = orderB.size.subtract(orderA.size);
            sizeFilled = orderA.size;
            orderA.size = new BigDecimal(0);
        }
        if (orderA.bid) {
            bid = orderA;
            ask = orderB;
        } else {
            ask = orderA;
            bid = orderB;
        }
        return new Match(orderB.ID,ask, bid, sizeFilled, this.price);
    }

    public String toString() {
        return String.format("price: %.2f | volume: %.2f", price, totalVolume);
    }
}
