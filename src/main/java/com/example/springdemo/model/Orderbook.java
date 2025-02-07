package com.example.springdemo.model;
import java.math.BigDecimal;
import java.util.*;

public class Orderbook {
    SortedSet<Limit> asks;
    SortedSet<Limit> bids;
    HashMap<BigDecimal, Limit> AskLimits;
    HashMap<BigDecimal, Limit> BidLimits;
    HashMap<UUID,Order> orders;
    ArrayList<Trade> trades;

    public Orderbook() {
        asks = new TreeSet<>((a, b) -> a.price.compareTo(b.price));
        bids = new TreeSet<>((a, b) -> b.price.compareTo(a.price));
        trades = new ArrayList<>();
        AskLimits = new HashMap<>();
        BidLimits = new HashMap<>();
        orders = new HashMap<>();
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
        System.out.printf("Clearing Limit price level [%.2f]%n",limit.price);
    }
}
