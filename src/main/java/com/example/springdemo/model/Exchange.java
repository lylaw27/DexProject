package com.example.springdemo.model;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;


@Component
public class Exchange{

    HashMap<String, User> users;
    HashMap<Market,Orderbook> orderbooks;
    Web3j web3;
    public Exchange(){
        orderbooks = new HashMap<>();
        users = new HashMap<>();
        ArrayList<String> newUserList = new ArrayList<>();
        newUserList.add("0x8b3a350cf5c34c9194ca85829a2df0ec3153be0318b5e2d3348e872092edffba");
        newUserList.add("0x92db14e403b83dfe3df233f83dfa3a0d7096f21ca9b0d6d6b8d88b2b4ec1564e");
        newUserList.add("0x47e179ec197488593b187f80a00eb0da91f1b9d0b13f8733639f19c30a34926a");
        for (String user : newUserList){
            addUser(user);
        }
        orderbooks.put(Market.ETH,new Orderbook());
        users.get("0x9965507d1a55bcc2695c58ba16fb37d819b0a4dc").setShares(BigDecimal.valueOf(10000));
        users.get("0x976ea74026e726554db657fa54763abd0c3a0aa9").setShares(BigDecimal.valueOf(10000));
    }

    public void StartServer(Web3j web3){
        this.web3 = web3;
    }
    public ConcurrentSkipListSet<Limit> HandleGetAskLimits(Market market){
        return orderbooks.get(market).asks;
    }
    public ConcurrentSkipListSet<Limit> HandleGetBuyLimits(Market market){
        return orderbooks.get(market).bids;
    }
    public void HandlePlaceMarketOrder(Market market, Order order) {
        BigDecimal totalSizeFilled = new BigDecimal(0);
        BigDecimal sumPrice = new BigDecimal(0);
        Orderbook orderbook = orderbooks.get(market);
        order.user.addOrder(order);
        ArrayList<Match> matches = orderbook.PlaceMarketOrder(order);
        HandleMatches(matches);
        for(Match match:matches){
            totalSizeFilled = totalSizeFilled.add(match.sizeFilled);
            sumPrice = sumPrice.add(match.price);
            orderbook.trades.addFirst(new Trade(match.price,match.sizeFilled,order.bid));
            if(orderbook.trades.size() > 30){
                orderbook.trades.removeLast();
            }
        }
        BigDecimal avgPrice = sumPrice.divide(new BigDecimal(matches.size()),2, RoundingMode.HALF_UP);
        order.price = avgPrice;
        orderbook.handleCandle(avgPrice);
//        System.out.printf("filled MARKET order -> %s | size: [%f] | avgPrice: [%f]%n",order.ID,totalSizeFilled,avgPrice);
    }

    public BigDecimal HandleGetBestBid(Market market){
        Orderbook orderbook = orderbooks.get(market);
        if(orderbook.bids.isEmpty()){
            return new BigDecimal(0);
        }
        return orderbook.bids.getFirst().price;
    }

    public BigDecimal HandleGetBestAsk(Market market){
        Orderbook orderbook = orderbooks.get(market);
        if(orderbook.asks.isEmpty()){
            return new BigDecimal(0);
        }
        return orderbook.asks.getFirst().price;
    }

    public void HandlePlaceLimitOrder(Market market,BigDecimal price, Order order) {
        Orderbook orderbook = orderbooks.get(market);
        orderbook.PlaceLimitOrder(price.setScale(2, RoundingMode.HALF_EVEN), order);
        order.user.addOrder(order);
//        System.out.printf("new LIMIT order -> type:[%b] | price: [%2f] | size: [%2f]%n",order.bid,price,order.size);
    }

    public UUID HandlePlaceOrder(PlaceOrderRequest placeOrderData) {
        Market market = placeOrderData.getMarket();
        Orderbook orderbook = orderbooks.get(market);
        User user = users.get(placeOrderData.getUserId().toLowerCase());
        UUID orderId = UUID.randomUUID();
        BigDecimal bestAsk = HandleGetBestAsk(market);
        BigDecimal bestBid = HandleGetBestBid(market);
        Order order = new Order(orderId,user,placeOrderData.getBid(), placeOrderData.getSize());
        orderbook.orders.put(order.ID,order);
        if(placeOrderData.getOrderType() == OrderType.LIMIT && ((bestBid.compareTo(BigDecimal.ZERO) == 0 || bestAsk.compareTo(BigDecimal.ZERO) == 0 ||placeOrderData.bid && placeOrderData.price.compareTo(bestAsk) < 0) || (!placeOrderData.bid && placeOrderData.price.compareTo(bestBid) > 0))){
            try{
                HandlePlaceLimitOrder(market,placeOrderData.getPrice(), order);
                return order.ID;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try{
                HandlePlaceMarketOrder(market,order);
                return order.ID;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public GetOrderData HandleGetOrderbook(Market market){
        Orderbook orderbook = orderbooks.get(market);
        GetOrderData getOrderData = new GetOrderData(orderbook);
        for(Limit limit:orderbook.asks){
            for(Order order: limit.orderList){
                getOrderData.getAsks().add(order);
            }
        }
        for(Limit limit:orderbook.bids){
            for(Order order: limit.orderList){
                getOrderData.getBids().add(order);
            }
        }
        return getOrderData;
    }

    public void HandleCancelOrder(Market market,UUID orderId){
        Orderbook orderbook = orderbooks.get(market);
        orderbook.CancelOrder(orderId);
    }

    public void HandleMatches(ArrayList<Match> matches){
        for(Match match:matches){
            User bidUser = users.get(match.bid.user.userId);
            User askUser = users.get(match.ask.user.userId);
            BigDecimal amount = match.sizeFilled.multiply(match.price);
            bidUser.shares = bidUser.shares.add(match.sizeFilled);
            BigDecimal oldShare = askUser.shares;
            askUser.shares = askUser.shares.subtract(match.sizeFilled);
            bidUser.purchasedAmount = bidUser.purchasedAmount.add(amount);
            askUser.purchasedAmount = askUser.purchasedAmount.multiply(askUser.shares).divide(oldShare, 2, RoundingMode.HALF_UP);
            ETHTransaction.TransferETH(web3,bidUser, askUser,amount);
        }
    }

    public void addUser(String privateKey){
        User user = new User(privateKey);
        users.put(user.userId,user);
    }

    public User HandleGetOrders(String userId){
        return users.get(userId);
    }

    public LinkedBlockingDeque<Trade> HandleGetTrades(Market market){
        Orderbook orderbook = orderbooks.get(market);
        return orderbook.trades;
    }

    public LinkedBlockingDeque<Candle> HandleGetCandles(Market market){
        Orderbook orderbook = orderbooks.get(market);
        return orderbook.candleList;
    }
}

