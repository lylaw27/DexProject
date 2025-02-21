package com.example.springdemo.controller;

import com.example.springdemo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Deque;
import java.util.SortedSet;

@EnableScheduling
@Controller
public class wsController {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    Exchange exchange;
    User userInfo;
    @Scheduled(fixedRate = 1000)
    public void sendAsks(){
        SortedSet<Limit> limits = exchange.HandleGetAskLimits(Market.ETH);
        template.convertAndSend("/orderbook/asks", limits);
    }

    @Scheduled(fixedRate = 1000)
    public void sendBids(){
        SortedSet<Limit> limits = exchange.HandleGetBuyLimits(Market.ETH);
        template.convertAndSend("/orderbook/bids", limits);
    }

    @Scheduled(fixedRate = 1000)
    public void sendPrice(){
        Deque<Trade> trades = exchange.HandleGetTrades(Market.ETH);
        template.convertAndSend("/orderbook/trades", trades);
    }

    @MessageMapping("/getuser")
    @SendTo("/orderbook/user")
    public User getUserId(String userId){
        userInfo = exchange.HandleGetOrders(userId.toLowerCase());
        return exchange.HandleGetOrders(userId.toLowerCase());
//        template.convertAndSend("/orderbook/user", userInfo);
    }

    @Scheduled(fixedRate = 1000)
    public void sendUser(){
        if(userInfo != null){
            template.convertAndSend("/orderbook/user", userInfo);
        }
    }

}
