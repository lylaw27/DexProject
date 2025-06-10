package com.example.springdemo.model;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class Orderbook {
    ConcurrentSkipListSet<Limit> asks;
    ConcurrentSkipListSet<Limit> bids;
    HashMap<BigDecimal, Limit> AskLimits;
    HashMap<BigDecimal, Limit> BidLimits;
    HashMap<UUID,Order> orders;
    LinkedBlockingDeque<Candle> candleList;
    LinkedBlockingDeque<Trade> trades;

    public Orderbook() {
        asks = new ConcurrentSkipListSet<>((a, b) -> a.price.compareTo(b.price));
        bids = new ConcurrentSkipListSet<>((a, b) -> b.price.compareTo(a.price));
        trades = new LinkedBlockingDeque<>();
        AskLimits = new HashMap<>();
        BidLimits = new HashMap<>();
        orders = new HashMap<>();
        candleList = new LinkedBlockingDeque<>();
    }

    public void PlaceLimitOrder(BigDecimal price, Order order) {
        Limit limit;
        if (order.bid) {
                limit = this.BidLimits.get(price);
        } else {
            limit = this.AskLimits.get(price);
        }
        if (limit == null) {
            limit = new Limit(price);
            if (order.bid) {
                this.bids.add(limit);
                this.BidLimits.put(price, limit);
            } else {
                this.asks.add(limit);
                this.AskLimits.put(price, limit);
            }
        }
        limit.AddOrder(order);
    }

    public ArrayList<Match> PlaceMarketOrder(Order order) {
        ArrayList<Match> matches = new ArrayList<>();
        ArrayList<Limit> emptyLimits = new ArrayList<>();
        if (order.bid) {
            if (order.size.compareTo(this.AskTotalVolume()) > 0) {
                System.err.printf("not enough total volume [size: %.2f] for market order [size: %.2f]", this.AskTotalVolume(), order.size);
            }
            for (Limit limit : this.asks) {
                ArrayList<Match> limitMatches = limit.Fill(order);
                matches.addAll(limitMatches);
                if (limit.totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                    emptyLimits.add(limit);
                }
                if(order.isFilled()){
                    break;
                }
            }
        } else {
            if (order.size.compareTo(this.BidTotalVolume()) > 0) {
                System.err.printf("not enough total volume [size: %.2f] for market order [size: %.2f]", this.AskTotalVolume(), order.size);
            }
            for (Limit limit : this.bids) {
                ArrayList<Match> limitMatches = limit.Fill(order);
                matches.addAll(limitMatches);
                if (limit.totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                    emptyLimits.add(limit);
                }
                if(order.isFilled()){
                    break;
                }
            }
        }
        for (Limit limit : emptyLimits) {
            this.ClearLimit(!order.bid, limit);
        }
        return matches;
    }

    public void CancelOrder(UUID orderId) {
        Order order = orders.get(orderId);
        Limit limit = order.limit;
        limit.DeleteOrder(order);
        order.user.orders.remove(order);
        if (limit.totalVolume.compareTo(BigDecimal.ZERO) == 0) {
            this.ClearLimit(order.bid, limit);
        }
    }

    public BigDecimal BidTotalVolume() {
        BigDecimal totalVolume = new BigDecimal(0);
        for (Limit limit : this.bids) {
            totalVolume = totalVolume.add(limit.totalVolume);
        }
        return totalVolume;
    }

    public BigDecimal AskTotalVolume() {
        BigDecimal totalVolume = new BigDecimal(0);
        for (Limit limit : this.asks) {
            totalVolume = totalVolume.add(limit.totalVolume);
        }
        return totalVolume;
    }

    public void ClearLimit(boolean bid, Limit limit) {
        if (bid) {
            this.bids.remove(limit);
            this.BidLimits.remove(limit.price);
        } else {
            this.asks.remove(limit);
            this.AskLimits.remove(limit.price);
        }
//        System.out.printf("Clearing Limit price level [%.2f]%n",limit.price);
    }

    public void handleCandle(BigDecimal price) {
        long timestamp = Instant.now().toEpochMilli()/60000*60000;
        if(candleList.isEmpty() || candleList.getLast().timestamp != timestamp){
            Candle candle = new Candle(price,timestamp);
            candleList.addLast(candle);
            if(candleList.size()>100){
                candleList.removeFirst();
            }
        }
        else{
            candleList.getLast().update(price);
        }
    }
}
