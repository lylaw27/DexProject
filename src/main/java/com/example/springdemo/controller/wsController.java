package com.example.springdemo.controller;

import com.example.springdemo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Deque;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;

@EnableScheduling
@Controller
public class wsController {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    Exchange exchange;
    HashMap<String,UserInfo> connectedUsers = new HashMap<>();
    @Scheduled(fixedRate = 1000)
    public void sendAsks(){
        ConcurrentSkipListSet<Limit> limits = exchange.HandleGetAskLimits(Market.ETH);
        template.convertAndSend("/orderbook/asks", limits);
    }

    @Scheduled(fixedRate = 1000)
    public void sendBids(){
        ConcurrentSkipListSet<Limit> limits = exchange.HandleGetBuyLimits(Market.ETH);
        template.convertAndSend("/orderbook/bids", limits);
    }

    @Scheduled(fixedRate = 1000)
    public void sendPrice(){
        LinkedBlockingDeque<Trade> trades = exchange.HandleGetTrades(Market.ETH);
        template.convertAndSend("/orderbook/trades", trades);
    }

    @Scheduled(fixedRate = 1000)
    public void sendCandles(){
        LinkedBlockingDeque<Candle> candles = exchange.HandleGetCandles(Market.ETH);
        if(!candles.isEmpty()){
            template.convertAndSend("/orderbook/candles", candles);
        }
    }

    @SubscribeMapping("/user/{userId}")
    public UserInfo getUserId(@DestinationVariable String userId) {
        UserInfo userInfo = exchange.HandleGetOrders(userId.toLowerCase());
        connectedUsers.put(userId,userInfo);
        return userInfo;
    }

    @Scheduled(fixedRate = 1000)
    public void sendUser(){
        connectedUsers.forEach((userId,userInfo)->{
            template.convertAndSend("/orderbook/user/"+userId, userInfo);
        });
    }
}
