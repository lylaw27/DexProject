package com.example.springdemo;

import com.example.springdemo.model.Client;
import com.example.springdemo.model.Market;
import com.example.springdemo.model.OrderType;
import com.example.springdemo.model.PlaceOrderRequest;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OrderGenerator {
    public static void GenerateOrders() throws InterruptedException {
        Client client = new Client();
        Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));
        String userId = "0x704226575Ea3B63AA30e6e4D683Dd81853F4E62F";
        client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,true,new BigDecimal("50"),new BigDecimal("9.1"),Market.ETH));
        client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,false,new BigDecimal("50"),new BigDecimal("10.2"),Market.ETH));
        TimeUnit.SECONDS.sleep(1);
        Thread thread1 = new Thread(()->{
            try {
                while(true){
                    PriceRandomizer(client,web3);
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(()->{
            try {
                while(true){
                MarketOrderPlacer(client,web3);
                TimeUnit.MILLISECONDS.sleep(500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread2.start();
    }


    public static void PriceRandomizer(Client client,Web3j web3) throws InterruptedException,ExecutionException {
        String user1 = "0x704226575Ea3B63AA30e6e4D683Dd81853F4E62F";
//        String user2 = "0x4fFCA2fe1BcB22d8bbA7bfEaCc95c734C65780d7";
//        String userId = user1;
//        BigInteger user1Balance = web3.ethGetBalance(user1, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
//        BigInteger user2Balance = web3.ethGetBalance(user2, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
//        if(user1Balance.compareTo(user2Balance)<0){
//            userId = user2;
//        }
        Random rand = new Random();
        int rand_bid = rand.nextInt(10);
        BigDecimal bestBid = client.GetBestBid();
        BigDecimal bestAsk = client.GetBestAsk();
        BigDecimal ranBidPrice = bestBid.add(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal ranAskPrice = bestAsk.subtract(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_EVEN);
        if(!(bestAsk.compareTo(ranBidPrice) > 0 && rand_bid>7)){
            ranBidPrice = BigDecimal.valueOf(9.1);
        }
        bestBid = client.GetBestBid();
        int rand_ask = rand.nextInt(10);
        if(!(bestBid.compareTo(ranAskPrice) < 0 && rand_ask>7)){
            ranAskPrice = BigDecimal.valueOf(10.2);
        }
        client.PlaceOrder(new PlaceOrderRequest(user1,OrderType.LIMIT,true,new BigDecimal("0.3"),ranBidPrice,Market.ETH));
        client.PlaceOrder(new PlaceOrderRequest(user1,OrderType.LIMIT,false,new BigDecimal("0.3"),ranAskPrice,Market.ETH));
    }






    public static void MarketOrderPlacer(Client client,Web3j web3) throws InterruptedException,ExecutionException {
        String user1 = "0x704226575Ea3B63AA30e6e4D683Dd81853F4E62F";
        String user2 = "0x4fFCA2fe1BcB22d8bbA7bfEaCc95c734C65780d7";
        boolean bid = true;
        BigInteger user1Balance = web3.ethGetBalance(user1, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
        BigInteger user2Balance = web3.ethGetBalance(user2, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
        if (user1Balance.compareTo(user2Balance) > 0) {
            bid = false;
        }
        client.PlaceOrder(new PlaceOrderRequest(user2, OrderType.MARKET, bid, new BigDecimal("0.05"), new BigDecimal(0), Market.ETH));
        System.out.println("exchange price: " + client.GetTrades().getLast().getPrice());
        TimeUnit.SECONDS.sleep(1);
//        client.PlaceOrder(new PlaceOrderRequest(userId, OrderType.MARKET, false, new BigDecimal("0.05"), new BigDecimal(8000), Market.ETH));
//        System.out.println("exchange price: " + client.GetTrades().getLast().getPrice());
//        TimeUnit.SECONDS.sleep(2);
    }
}

