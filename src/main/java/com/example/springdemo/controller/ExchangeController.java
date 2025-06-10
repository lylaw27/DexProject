package com.example.springdemo.controller;


import com.example.springdemo.model.*;
import com.example.springdemo.service.ExchangeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
public class ExchangeController {

    @Autowired
    ExchangeService service;

    @PostMapping("/order")
    public synchronized UUID HandlePlaceOrder(@RequestBody PlaceOrderRequest payload){
        return service.HandlePlaceOrder(payload);
    }

    @GetMapping("/book/{market}")
    public GetOrderData HandleGetBook(@PathVariable Market market){
        return service.HandleGetBook(market);
    }

    @GetMapping("/book/{market}/bids")
    public GetOrderData HandleAllBid(@PathVariable Market market){
        return service.HandleGetBook(market);
    }

    @GetMapping("/book/{market}/asks")
    public GetOrderData HandleAllAsk(@PathVariable Market market){
        return service.HandleGetBook(market);
    }

    @GetMapping("/book/{market}/bestBid")
    public BigDecimal HandleGetBestBid(@PathVariable Market market) {
        return service.HandleGetBestBid(market);
    }

    @GetMapping("/book/{market}/bestAsk")
    public BigDecimal HandleGetBestAsk(@PathVariable Market market){
        return service.HandleGetBestAsk(market);
    }

    @GetMapping("/order/{userId}")
    public User HandleGetOrders(@PathVariable String userId){
        return service.HandleGetOrders(userId);
    }

    @GetMapping("/trades/{market}")
    public Deque<Trade> HandleGetTrades(@PathVariable Market market){
        return service.HandleGetTrades(market);
    }

    @DeleteMapping("/book/{market}/{orderId}")
    public String HandleCancelOrder(@PathVariable Market market, @PathVariable UUID orderId){
        service.HandleCancelOrder(market,orderId);
        return "Order cancelled!";
    }

}
