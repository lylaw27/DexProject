package com.example.springdemo;

import com.example.springdemo.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OrderGenerator {
    public static void GenerateOrders() throws InterruptedException {
        Client client = new Client();
        String userId = "0x9965507D1a55bcC2695C58ba16FB37d819B0A4dc";
        for(BigDecimal i = BigDecimal.ZERO; i.compareTo(BigDecimal.valueOf(0.5)) < 0; i = i.add(BigDecimal.valueOf(0.01))){
            BigDecimal orderSize = BigDecimal.valueOf(Math.pow(Math.random(),3)*5 + 0.05).setScale(2, RoundingMode.HALF_EVEN);
            client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,true,orderSize,BigDecimal.valueOf(40).add(i),Market.ETH));
            client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,false,orderSize,BigDecimal.valueOf(41).subtract(i),Market.ETH));
        }
        TimeUnit.SECONDS.sleep(1);
        Thread thread1 = new Thread(()->{
        while(true){
            try {
                BuyOrderPlacer(client);
                TimeUnit.MILLISECONDS.sleep(Math.round(Math.random()*2000));
            }
            catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        });
        Thread thread2 = new Thread(()->{
        while(true){
            try {
                SellOrderPlacer(client);
                TimeUnit.MILLISECONDS.sleep(Math.round(Math.random()*2000));
            }
            catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        TimeUnit.MILLISECONDS.sleep(200);
        thread2.start();
    }


    public static void BuyOrderPlacer(Client client) throws InterruptedException,ExecutionException {
        String user1 = "0x9965507D1a55bcC2695C58ba16FB37d819B0A4dc";
        Random rand = new Random();
        int rand_bid = rand.nextInt(10);
        BigDecimal bestBid = client.GetBestBid();
        BigDecimal orderSize = BigDecimal.valueOf(Math.pow(Math.random(),3)*5 + 0.05).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal ranBidPrice = bestBid.add(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_EVEN);
        if(rand_bid>6){
            ranBidPrice = bestBid.subtract(BigDecimal.valueOf(Math.pow(Math.random(),6))).setScale(2, RoundingMode.HALF_EVEN);
            orderSize = BigDecimal.valueOf(Math.pow(Math.random(),5)*5 + 0.05).setScale(2, RoundingMode.HALF_EVEN);
        }
            client.PlaceOrder(new PlaceOrderRequest(user1,OrderType.LIMIT,true,orderSize,ranBidPrice,Market.ETH));
    }

    public static void SellOrderPlacer(Client client) throws InterruptedException,ExecutionException {
        String user1 = "0x9965507D1a55bcC2695C58ba16FB37d819B0A4dc";
        Random rand = new Random();
        int rand_ask = rand.nextInt(10);
        BigDecimal bestAsk = client.GetBestAsk();
        BigDecimal orderSize = BigDecimal.valueOf(Math.pow(Math.random(),3)*5 + 0.05).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal ranAskPrice = bestAsk.subtract(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_EVEN);
        if(rand_ask>6){
            ranAskPrice = bestAsk.add(BigDecimal.valueOf(Math.pow(Math.random(),6))).setScale(2, RoundingMode.HALF_EVEN);
            orderSize = BigDecimal.valueOf(Math.pow(Math.random(),5)*5 + 0.05).setScale(2, RoundingMode.HALF_EVEN);
        }
        client.PlaceOrder(new PlaceOrderRequest(user1,OrderType.LIMIT,false,orderSize,ranAskPrice,Market.ETH));
    }

}

