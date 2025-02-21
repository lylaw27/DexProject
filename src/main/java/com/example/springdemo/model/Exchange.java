package com.example.springdemo.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Component
public class Exchange{
    HashMap<String, User> users;
    HashMap<Market,Orderbook> orderbooks;
    Market market;
    Web3j web3;
    public Exchange(){
        orderbooks = new HashMap<>();
        users = new HashMap<>();
        ArrayList<String> newUserList = new ArrayList<>();
        newUserList.add("0xf42fc529de18db578ef6c426fd0ac71eaaa7b6912be9afc4f126352aeed8f208");
        newUserList.add("0xf72ff945fdc260d44c6311cc5f93b1100be968d950aee1241fe4a8949386ef29");
        newUserList.add("0xeecb515ba37dba50453eb1aacca6d7ef5f9033540d9a9f82993ff530b21db7dc");
        for (String user : newUserList){
            addUser(user);
        }
        orderbooks.put(Market.ETH,new Orderbook());
        users.get("0x704226575ea3b63aa30e6e4d683dd81853f4e62f").setShares(BigDecimal.valueOf(100));
        users.get("0x4ffca2fe1bcb22d8bba7bfeacc95c734c65780d7").setShares(BigDecimal.valueOf(100));
    }

    public void StartServer(Web3j web3){
        this.web3 = web3;
    }
    public SortedSet<Limit> HandleGetAskLimits(Market market){
        return orderbooks.get(market).asks;
    }
    public SortedSet<Limit> HandleGetBuyLimits(Market market){
        return orderbooks.get(market).bids;
    }
    public void HandlePlaceMarketOrder(Market market, Order order) {
        BigDecimal totalSizeFilled = new BigDecimal(0);
        BigDecimal sumPrice = new BigDecimal(0);
        Orderbook orderbook = orderbooks.get(market);
        order.user.orders.add(order);
        ArrayList<Match> matches = orderbook.PlaceMarketOrder(order);
        HandleMatches(matches);
        for(Match match:matches){
            totalSizeFilled = totalSizeFilled.add(match.sizeFilled);
            sumPrice = sumPrice.add(match.price);
            orderbook.trades.addFirst(new Trade(match.price,match.sizeFilled,order.bid));
        }
        BigDecimal avgPrice = sumPrice.divide(new BigDecimal(matches.size()),2, RoundingMode.HALF_UP);
        order.price = avgPrice;
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
        order.user.orders.add(order);
        System.out.printf("new LIMIT order -> type:[%b] | price: [%2f] | size: [%2f]%n",order.bid,price,order.size);
    }

    public UUID HandlePlaceOrder(PlaceOrderRequest placeOrderData) {
        market = placeOrderData.getMarket();
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
            bidUser.fmv = bidUser.fmv.add(amount);
            askUser.fmv = askUser.fmv.multiply(askUser.shares).divide(oldShare, 2, RoundingMode.HALF_UP);
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

    public Deque<Trade> HandleGetTrades(Market market){
        Orderbook orderbook = orderbooks.get(market);
        return orderbook.trades;
    }
}

