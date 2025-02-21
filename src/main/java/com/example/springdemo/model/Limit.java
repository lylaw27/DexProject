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

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    void AddOrder(Order order) {
        order.limit = this;
        order.price = this.price;
        orderList.add(order);
        totalVolume = totalVolume.add(order.size);
    }

    void DeleteOrder(Order order) {
        orderList.remove(order);
        totalVolume = totalVolume.subtract(order.size);
    }

    ArrayList<Match> Fill(Order incomingOrder) {
        ArrayList<Match> matches = new ArrayList<>();
        for (Order bookOrder : this.orderList) { //loop through the limits
            Match match = this.FillOrder(incomingOrder, bookOrder);
            matches.add(match);
            totalVolume = totalVolume.subtract(match.sizeFilled);
            if (bookOrder.isFilled()) {
                this.DeleteOrder(bookOrder);
            }
            if (incomingOrder.isFilled()) {
                break;
            }
        }
        return matches;
    }

    Match FillOrder(Order orderA, Order orderB) {
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

}
