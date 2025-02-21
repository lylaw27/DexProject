package com.example.springdemo;

import com.example.springdemo.model.Client;
import com.example.springdemo.model.Market;
import com.example.springdemo.model.OrderType;
import com.example.springdemo.model.PlaceOrderRequest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarketMaker {
    public static void makeMarket() throws InterruptedException {
        Client client = new Client();
        client.PlaceOrder(new PlaceOrderRequest("0x4344DE8a9a7C372144EbEAA0d993208Fbcb1B709", OrderType.LIMIT,false,new BigDecimal("1"),new BigDecimal(10000), Market.ETH));
        client.PlaceOrder(new PlaceOrderRequest("0x4344DE8a9a7C372144EbEAA0d993208Fbcb1B709",OrderType.LIMIT,true,new BigDecimal("1"),new BigDecimal(9000),Market.ETH));
        TimeUnit.SECONDS.sleep(1);
        Thread thread1 = new Thread(()->{
            try {
                SimpleMarketMaker(client);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread2 = new Thread(()->{
            try {
                MarketOrderPlacer(client);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread2.start();
    }
    public static void SimpleMarketMaker(Client client) throws InterruptedException {
        String userId = "0x704226575Ea3B63AA30e6e4D683Dd81853F4E62F";

        for(int i = 0;i<1000;i++){
            BigDecimal bestBid = client.GetBestBid();
            BigDecimal bestAsk = client.GetBestAsk();
            BigDecimal spread = bestBid.subtract(bestAsk).abs();
            System.out.println(bestBid);
            System.out.println(bestAsk);
            System.out.println(spread);
            if(client.GetOrders(userId).getOrders().size()<3){
                BigDecimal bidPrice = bestBid.add(new BigDecimal(100));
                UUID bidOrderId = client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,true,new BigDecimal("0.1"),bidPrice,Market.ETH));
            }
            if(client.GetOrders(userId).getOrders().size()<3) {
                BigDecimal askPrice = bestAsk.subtract(new BigDecimal(100));
                UUID askOrderId = client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,false,new BigDecimal("0.1"),askPrice,Market.ETH));
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }
    public static void MarketOrderPlacer(Client client) throws InterruptedException{
        for(int i = 0;i<20;i++){
            client.PlaceOrder(new PlaceOrderRequest("0x4fFCA2fe1BcB22d8bbA7bfEaCc95c734C65780d7",OrderType.MARKET,true,new BigDecimal("0.05"),new BigDecimal(8000),Market.ETH));
            System.out.println("exchange price: " + client.GetTrades().getLast().getPrice());
            client.PlaceOrder(new PlaceOrderRequest("0x4fFCA2fe1BcB22d8bbA7bfEaCc95c734C65780d7",OrderType.MARKET,false,new BigDecimal("0.1"),new BigDecimal(8000),Market.ETH));
            System.out.println("exchange price: " + client.GetTrades().getLast().getPrice());
            TimeUnit.SECONDS.sleep(4);
        }
    }
}

