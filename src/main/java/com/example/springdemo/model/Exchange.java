package com.example.springdemo.model;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;



public class Exchange{
    HashMap<String, User> users;
    Credentials exchangeCredentials;
    HashMap<Market,Orderbook> orderbooks;
    Market market;
    Web3j web3;
    public Exchange(Credentials exchangeCredentials,Web3j web3){
        orderbooks = new HashMap<>();
        users = new HashMap<>();
        addUser("0x2ceb72c34ad157b00082698ea4b1813e7904a7bc154296a53299c8e0f45566c1");
        addUser("0x659bddda8c37f9dcaaa283268127a08a58dd97e42854e2b43457d69f0a6e7a24");
        addUser("0xa3b21763987d0bc52aa8150af25b850a108fded318b2cc87fa6fdafe818937ed");
        addUser("0x4e9be8e31adb07901c1e640fd2bf35147ba1c696fa4fcde8cb4fb5c49a03d3c4");
        orderbooks.put(Market.ETH,new Orderbook());
        this.web3 = web3;
        this.exchangeCredentials = exchangeCredentials;
    }

    public void HandlePlaceMarketOrder(Market market, Order order) {
        BigDecimal totalSizeFilled = new BigDecimal(0);
        BigDecimal sumPrice = new BigDecimal(0);
        Orderbook orderbook = orderbooks.get(market);
        ArrayList<Match> matches = orderbook.PlaceMarketOrder(order);
        HandleMatches(matches);
        for(Match match:matches){
            totalSizeFilled = totalSizeFilled.add(match.sizeFilled);
            sumPrice = sumPrice.add(match.price);
        }
        BigDecimal avgPrice = sumPrice.divide(new BigDecimal(matches.size()),2, RoundingMode.HALF_UP);
        System.out.printf("filled MARKET order -> %s | size: [%f] | avgPrice: [%f]%n",order.ID,totalSizeFilled,avgPrice);
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
        orderbook.PlaceLimitOrder(price, order);
        if(order.bid){
            order.user.bids.add(order);
        }
        else{
            order.user.asks.add(order);
        }
        System.out.printf("new LIMIT order -> type:[%b] | price: [%f] | size: [%f]%n",order.bid,price,order.size);
    }

    public UUID HandlePlaceOrder(PlaceOrderRequest placeOrderData) {
        market = placeOrderData.getMarket();
        Orderbook orderbook = orderbooks.get(market);
        User user = users.get(placeOrderData.getUserId().toLowerCase());
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId,user,placeOrderData.getBid(), placeOrderData.getSize());
        orderbook.orders.put(order.ID,order);
        if(placeOrderData.getOrderType() == OrderType.LIMIT){
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
            Credentials toUser = users.get(match.bid.user.userId).getCredentials();
            Credentials fromUser = users.get(match.ask.user.userId).getCredentials();
            BigDecimal amount = match.sizeFilled;
            ETHTransaction.TransferETH(web3,fromUser, toUser,amount);
        }
    }

    public void addUser(String privateKey){
        User user = new User(privateKey);
        users.put(user.userId,user);
    }

    public User HandleGetOrders(String userId){
        return users.get(userId);
    }
}

