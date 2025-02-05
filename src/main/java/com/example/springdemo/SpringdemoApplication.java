package com.example.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.springdemo.model.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class SpringdemoApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringdemoApplication.class, args);
        Client client = new Client();
            client.PlaceOrder(new PlaceOrderRequest("0xac8386e95487E75a3A104247c41985D553c5Cb79",OrderType.LIMIT,false,new BigDecimal("1"),new BigDecimal(10000),Market.ETH));
            client.PlaceOrder(new PlaceOrderRequest("0x16D0eEF1407D7bc8630B39F102Dcf8487b1c9682",OrderType.LIMIT,true,new BigDecimal("1"),new BigDecimal(9000),Market.ETH));
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
        String userId = "0x3eA514E01181D41dd3866eD81E09033B4d4252eB";

        for(int i = 0;i<20;i++){
            BigDecimal bestBid = client.GetBestBid();
            BigDecimal bestAsk = client.GetBestAsk();
            BigDecimal spread = bestBid.subtract(bestAsk).abs();
            System.out.println(bestBid);
            System.out.println(bestAsk);
            System.out.println(spread);
            if(client.GetOrders(userId).getBids().size()<3){
                BigDecimal bidPrice = bestBid.add(new BigDecimal(100));
                UUID bidOrderId = client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,true,new BigDecimal("0.1"),bidPrice,Market.ETH));
            }
            if(client.GetOrders(userId).getAsks().size()<3) {
                BigDecimal askPrice = bestAsk.subtract(new BigDecimal(100));
                UUID askOrderId = client.PlaceOrder(new PlaceOrderRequest(userId,OrderType.LIMIT,false,new BigDecimal("0.1"),askPrice,Market.ETH));
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }
        public static void MarketOrderPlacer(Client client) throws InterruptedException{
            for(int i = 0;i<20;i++){
                client.PlaceOrder(new PlaceOrderRequest("0x5222f25C03c7fCeae2aB00b207a1E972158163D7",OrderType.MARKET,true,new BigDecimal("0.05"),new BigDecimal(8000),Market.ETH));
                client.PlaceOrder(new PlaceOrderRequest("0x5222f25C03c7fCeae2aB00b207a1E972158163D7",OrderType.MARKET,false,new BigDecimal("0.1"),new BigDecimal(8000),Market.ETH));
                TimeUnit.SECONDS.sleep(4);
            }
        }

}
