package com.example.springdemo.service;
import com.example.springdemo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.UUID;


@RestController
public class ExchangeService {
    Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));

    @Autowired
    Exchange exchange;

    public ExchangeService(Exchange exchange){
        this.exchange = exchange;
        exchange.StartServer(web3);
    }
    public UUID HandlePlaceOrder(PlaceOrderRequest payload) {
        return exchange.HandlePlaceOrder(payload);
    }
    public GetOrderData HandleGetBook(Market market) {
        return exchange.HandleGetOrderbook(market);
    }
    public User HandleGetOrders(String userId){ String realUserId = userId.toLowerCase(); return exchange.HandleGetOrders(realUserId);}
    public BigDecimal HandleGetBestBid(Market market) {return exchange.HandleGetBestBid(market);}
    public BigDecimal HandleGetBestAsk(Market market) {
        return exchange.HandleGetBestAsk(market);
    }
    public Deque<Trade> HandleGetTrades(Market market) {
        return exchange.HandleGetTrades(market);
    }
    public void HandleCancelOrder(Market market,UUID orderId) {
        exchange.HandleCancelOrder(market,orderId);
    }
}
